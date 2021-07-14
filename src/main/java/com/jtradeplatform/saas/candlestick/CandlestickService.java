package com.jtradeplatform.saas.candlestick;

import com.jtradeplatform.saas.chart.PatternFinderContext;
import com.jtradeplatform.saas.chart.PatternResultContainer;
import com.jtradeplatform.saas.chart.patternsImpl.CascadePattern;
import com.jtradeplatform.saas.chart.patternsImpl.Speed30Pattern;
import com.jtradeplatform.saas.event.Event;
import com.jtradeplatform.saas.event.EventService;
import com.jtradeplatform.saas.services.BinanceSpotService;
import com.jtradeplatform.saas.symbol.Symbol;
import com.jtradeplatform.saas.symbol.SymbolRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

@Service
@AllArgsConstructor
public class CandlestickService {

    CandlestickRepository candlestickRepository;
    SymbolRepository symbolRepository;
    BinanceSpotService binanceSpot;
    PatternFinderContext patternFinderContext;
    SimpMessagingTemplate webSocket;
    EventService eventService;

    /**
     * find patterns in reversed candlesticks
     * TODO: refactoring
     */
    public void findPatternsAndSend() {

        List<Symbol> symbolList = symbolRepository.findAll();
        patternFinderContext.setPatternClasses(Arrays.asList(
                Speed30Pattern.class,
                CascadePattern.class
        ));

        int maxThreadCount = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreadCount);
        Collection<Future<?>> futures = new LinkedList<>();
        //
        for (Symbol symbol : symbolList) {
            futures.add(
                    executor.submit(() -> {
                        List<Candlestick> candlestickList = candlestickRepository.findAllBySymbol(symbol);
                        Collections.reverse(candlestickList);
                        List<PatternResultContainer> resultContainerList = patternFinderContext.find(candlestickList);

                        if (resultContainerList.isEmpty()) {
                            return;
                        }

                        resultContainerList.forEach(resultContainer -> {
                            if (!resultContainer.getSignal()) {
                                return;
                            }
                            Event event = new Event(symbol, resultContainer.toString());
                            eventService.saveAndSend(event, "/events");
                        });
                    })
            );
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                //e.printStackTrace();
                // TODO: разобраться с исключениями, часто возникают
            }
        }
    }

    public void updateAllCharts() {
        List<Symbol> symbolEntities = symbolRepository.findAll();
        for (Symbol symbol : symbolEntities) {
            this.updateSymbolChart(symbol);
        }
    }

    /**
     * TODO: refactoring
     */
    public void updateSymbolChart(Symbol symbol) {
        int limit = 1000;
        long to = Instant.now().toEpochMilli();
        Candlestick lastCandlestick = candlestickRepository.findLastBySymbol(symbol);
        Instant fromInstant = lastCandlestick.getTime();

        while (true) {
            try {
                List<Candlestick> candlesticks = binanceSpot.getSymbolHistory(symbol, limit, fromInstant.toEpochMilli(), to);
                candlestickRepository.saveAll(candlesticks);
                fromInstant = fromInstant.plus(limit, ChronoUnit.MINUTES);

                if (candlesticks.isEmpty()) break;

                Thread.sleep(80);
            } catch (Exception e) {
                System.err.println("Update chart error (" + symbol.getName() + "): " + e);
                break;
            }
        }
    }

    public void deleteAll() {
        candlestickRepository.deleteAll();
    }
}
