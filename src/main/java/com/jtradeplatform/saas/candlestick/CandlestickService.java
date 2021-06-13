package com.jtradeplatform.saas.candlestick;

import com.jtradeplatform.saas.services.BinanceSpotService;
import com.jtradeplatform.saas.symbol.Symbol;
import com.jtradeplatform.saas.symbol.SymbolRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CandlestickService {

    CandlestickRepository candlestickRepository;
    SymbolRepository symbolRepository;
    BinanceSpotService binanceSpot;
    SimpMessagingTemplate webSocket;
    Closeable watcher;

    private static final Map<String, Candlestick> candlestickQueue = new ConcurrentHashMap<>();

    CandlestickService(
            CandlestickRepository candlestickRepository,
            SymbolRepository symbolRepository,
            BinanceSpotService binanceSpot,
            SimpMessagingTemplate webSocket
    ) {
        this.candlestickRepository = candlestickRepository;
        this.symbolRepository = symbolRepository;
        this.binanceSpot = binanceSpot;
        this.webSocket = webSocket;
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
