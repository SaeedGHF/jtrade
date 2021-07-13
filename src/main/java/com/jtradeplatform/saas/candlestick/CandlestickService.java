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
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

@Service
public class CandlestickService {

    CandlestickRepository candlestickRepository;
    SymbolRepository symbolRepository;
    BinanceSpotService binanceSpot;
    PatternFinderContext patternFinderContext;
    SimpMessagingTemplate webSocket;
    EventService eventService;
    Closeable watcher;

    private static final Map<String, Candlestick> candlestickQueue = new ConcurrentHashMap<>();

    CandlestickService(
            CandlestickRepository candlestickRepository,
            SymbolRepository symbolRepository,
            BinanceSpotService binanceSpot,
            SimpMessagingTemplate webSocket,
            PatternFinderContext patternFinderContext,
            EventService eventService
    ) {
        this.candlestickRepository = candlestickRepository;
        this.symbolRepository = symbolRepository;
        this.binanceSpot = binanceSpot;
        this.webSocket = webSocket;
        this.eventService = eventService;
        this.patternFinderContext = patternFinderContext;
    }

    public static void addToQueue(Candlestick candlestick) {
        candlestickQueue.put(candlestick.toString(), candlestick);
    }

    public void updateAllCharts() {
        List<Symbol> symbolEntities = symbolRepository.findAll();
        for (Symbol symbol : symbolEntities) {
            this.updateSymbolChart(symbol);
        }
    }

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
            }
        }
    }

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

    public void runQueue() {
        synchronized (candlestickQueue) {
            List<Candlestick> list = new ArrayList<>(candlestickQueue.values());
            candlestickQueue.clear();
            candlestickRepository.saveAll(list);
        }
    }

    public void deleteAll() {
        candlestickRepository.deleteAll();
    }

    public void runWatcher() {
        if (watcher != null) {
            throw new RuntimeException("Watcher already exists!");
        }
        List<Symbol> symbolEntities = symbolRepository.findAll();
        List<String> symbolList = new ArrayList<>();
        Map<String, Integer> symbolMap = new HashMap<>();
        for (Symbol symbol : symbolEntities) {
            symbolList.add(symbol.getName());
            symbolMap.put(symbol.getName(), symbol.getId());
        }

        CandlestickHandler candlestickHandler = new CandlestickHandler(symbolMap, webSocket);
        watcher = binanceSpot.subscribeCandlesticks(symbolList, candlestickHandler);
    }

    public void stopWatcher() {
        if (watcher == null) {
            return;
        }
        try {
            watcher.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
