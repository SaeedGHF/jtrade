package com.jtradeplatform.saas.services;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.jtradeplatform.saas.configs.BinanceApiConfig;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.util.List;

@Service
public final class BinanceSpot {

    BinanceApiConfig binanceApiConfig;
    BinanceApiWebSocketClient wsClient;

    BinanceSpot(BinanceApiConfig binanceApiConfig) {
        this.binanceApiConfig = binanceApiConfig;
        this.wsClient = BinanceApiClientFactory.newInstance().newWebSocketClient();
    }

    public Closeable subscribeCandlesticks(List<String> symbols, CandlestickInterval interval, BinanceApiCallback<CandlestickEvent> handler) {
        String symbolsStr = String.join(",", symbols).toLowerCase();
        return wsClient.onCandlestickEvent(symbolsStr, interval, handler);
    }
}
