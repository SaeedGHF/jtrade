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
        this.runWatcher();
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
     */
    public void findPatternsAndSend() {

        List<Symbol> symbolList = symbolRepository.findAll();
        patternFinderContext.setPatternClasses(Arrays.asList(
                Speed30Pattern.class,
                CascadePattern.class
        ));

        int maxThreadCount = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreadCount);
        Collection<Future<?>> futures = new LinkedList<Future<?>>();
        //
        for (Symbol symbol : symbolList) {
            futures.add(
                    executor.submit(() -> {
                        List<Candlestick> candlestickList = candlestickRepository.findAllBySymbol(symbol.getId());
                        Collections.reverse(candlestickList);

                        PatternResultContainer resultContainer = patternFinderContext.find(candlestickList);

                        if (resultContainer.isEmpty()) {
                            return;
                        }

                        Event event = new Event(symbol, resultContainer.toString());
                        eventService.saveAndSend(event, "/events");
                    })
            );
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateSymbolChart(Symbol symbol) {
        int minutes = 3 * 24 * 60;
        int limit = 1000;
        long to = Instant.now().toEpochMilli();
        Instant fromInstant = Instant.now().minus(minutes, ChronoUnit.MINUTES);

        while (true) {
            try {
                List<Candlestick> candlesticks = binanceSpot.getSymbolHistory(symbol, limit, fromInstant.toEpochMilli(), to);
                candlestickRepository.saveAll(candlesticks);
                fromInstant = fromInstant.plus(limit, ChronoUnit.MINUTES);

                if (candlesticks.isEmpty()) break;

                Thread.sleep(100);
            } catch (Exception e) {
                System.err.println("Update error (" + symbol.getName() + "): " + e);
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
