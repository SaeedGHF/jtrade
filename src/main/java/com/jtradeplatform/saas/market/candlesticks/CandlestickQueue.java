package com.jtradeplatform.saas.market.candlesticks;

import com.jtradeplatform.saas.candlestick.Candlestick;
import com.jtradeplatform.saas.candlestick.CandlestickRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CandlestickQueue {

    CandlestickRepository candlestickRepository;
    private boolean enableSaveCandlesticks = true;
    private final Map<String, Candlestick> candlestickQueue = new ConcurrentHashMap<>();

    CandlestickQueue(CandlestickRepository repository) {
        candlestickRepository = repository;
    }

    public void run() {
        if (!enableSaveCandlesticks) {
            return;
        }
        synchronized (candlestickQueue) {
            List<Candlestick> list = new ArrayList<>(candlestickQueue.values());
            candlestickQueue.clear();
            candlestickRepository.saveAll(list);
        }
    }

    public void add(Candlestick candlestick) {
        candlestickQueue.put(candlestick.toString(), candlestick);
    }

    public void disableSavingCandlesticks() {
        enableSaveCandlesticks = false;
    }

    public void enableSavingCandlesticks() {
        enableSaveCandlesticks = true;
    }
}
