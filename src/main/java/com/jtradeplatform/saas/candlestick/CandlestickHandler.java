package com.jtradeplatform.saas.candlestick;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.domain.event.CandlestickEvent;

import java.sql.Timestamp;
import java.util.Map;

public class CandlestickHandler implements BinanceApiCallback<CandlestickEvent> {

    Map<String, Integer> symbolMap;

    CandlestickHandler(Map<String, Integer> symbolMap) {
        this.symbolMap = symbolMap;
    }

    @Override
    public void onResponse(CandlestickEvent e) {
        int symbolId = symbolMap.get(e.getSymbol());
        Candlestick candlestick = new Candlestick(
                new Timestamp(e.getOpenTime()).toInstant(),
                e.getIntervalId(),
                symbolId,
                Double.parseDouble(e.getOpen()),
                Double.parseDouble(e.getHigh()),
                Double.parseDouble(e.getLow()),
                Double.parseDouble(e.getClose())
        );
        CandlestickService.addToQueue(candlestick);
    }

    @Override
    public void onFailure(Throwable cause) {
        System.err.println("onFailure (candlestick): " + cause.toString());
    }
}
