package com.jtradeplatform.saas.market.candlesticks;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.jtradeplatform.saas.candlestick.Candlestick;
import com.jtradeplatform.saas.market.services.BinanceSpotService;
import com.jtradeplatform.saas.symbol.Symbol;
import com.jtradeplatform.saas.symbol.SymbolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Component
@RequiredArgsConstructor
public class CandlestickWatcher {

    Closeable watcher;
    Map<String, Integer> symbolMap;
    private final SymbolRepository symbolRepository;
    private final BinanceSpotService binanceSpot;
    private final SimpMessagingTemplate webSocket;
    private final CandlestickQueue candlestickQueue;

    public void run() throws IOException {
        List<Symbol> symbolEntities = symbolRepository.findAll();
        List<String> symbolList = new ArrayList<>();
        symbolMap = new HashMap<>();

        for (Symbol symbol : symbolEntities) {
            symbolList.add(symbol.getName());
            symbolMap.put(symbol.getName(), symbol.getId());
        }

        if (watcher != null) watcher.close();
        watcher = binanceSpot.subscribeCandlesticks(symbolList, new Handler());
    }

    private class Handler implements BinanceApiCallback<CandlestickEvent> {

        public void onResponse(CandlestickEvent e) {
            Integer symbolId = symbolMap.get(e.getSymbol());
            if (symbolId == null) {
                return;
            }
            Candlestick candlestick = new Candlestick(
                    new Timestamp(e.getOpenTime()).toInstant(),
                    e.getIntervalId(),
                    symbolId.toString(),
                    Double.parseDouble(e.getOpen()),
                    Double.parseDouble(e.getHigh()),
                    Double.parseDouble(e.getLow()),
                    Double.parseDouble(e.getClose())
            );

            webSocket.convertAndSend("/charts/" + candlestick.getSymbol(), candlestick);
            candlestickQueue.add(candlestick);
        }

        public void onFailure(Throwable cause) {
            System.err.println("onFailure (candlestick): " + cause.toString());
            cause.printStackTrace();
            try {
                Thread.sleep(5000);
                System.out.println("Try to run candlestick watcher...");
                run();
                System.out.println("Candlestick watcher restarted.");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}