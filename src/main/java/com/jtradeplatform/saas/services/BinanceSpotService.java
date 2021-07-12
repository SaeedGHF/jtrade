package com.jtradeplatform.saas.services;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.jtradeplatform.saas.candlestick.Candlestick;
import com.jtradeplatform.saas.configs.BinanceApiConfig;
import com.jtradeplatform.saas.symbol.Symbol;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public final class BinanceSpotService {

    BinanceApiConfig binanceApiConfig;
    BinanceApiWebSocketClient wsClient;
    BinanceApiRestClient restClient;

    private final CandlestickInterval DEFAULT_INTERVAL = CandlestickInterval.ONE_MINUTE;

    public BinanceSpotService(BinanceApiConfig binanceApiConfig) {
        this.binanceApiConfig = binanceApiConfig;
        this.wsClient = BinanceApiClientFactory.newInstance().newWebSocketClient();
        this.restClient = BinanceApiClientFactory.newInstance().newRestClient();
    }

    public Closeable subscribeCandlesticks(List<String> symbols,BinanceApiCallback<CandlestickEvent> handler) {
        String symbolsStr = String.join(",", symbols).toLowerCase();
        return wsClient.onCandlestickEvent(symbolsStr, DEFAULT_INTERVAL, handler);
    }

    public List<Candlestick> getSymbolHistory(Symbol symbol, int limit, long startTimeMS, long endTimeMS) {
        List<com.binance.api.client.domain.market.Candlestick> bCandlesticks = restClient.getCandlestickBars(
                symbol.getName().toUpperCase(),
                DEFAULT_INTERVAL,
                limit,
                startTimeMS,
                endTimeMS
        );
        List<Candlestick> result = new ArrayList<>();
        for(com.binance.api.client.domain.market.Candlestick c: bCandlesticks){
            Candlestick myC = new Candlestick(
                    new Timestamp(c.getOpenTime()).toInstant(),
                    DEFAULT_INTERVAL.getIntervalId(),
                    symbol.getId().toString(),
                    Double.parseDouble(c.getOpen()),
                    Double.parseDouble(c.getHigh()),
                    Double.parseDouble(c.getLow()),
                    Double.parseDouble(c.getClose())
            );
            result.add(myC);
        }

        return result;
    }
}
