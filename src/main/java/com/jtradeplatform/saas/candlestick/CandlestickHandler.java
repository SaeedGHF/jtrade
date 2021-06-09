package com.jtradeplatform.saas.candlestick;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.domain.event.CandlestickEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.sql.Timestamp;
import java.util.Map;

public class CandlestickHandler implements BinanceApiCallback<CandlestickEvent> {

    Map<String, Integer> symbolMap;
    SimpMessagingTemplate webSocket;


    CandlestickHandler(Map<String, Integer> symbolMap, SimpMessagingTemplate webSocket) {
        this.symbolMap = symbolMap;
        this.webSocket = webSocket;
    }

    @Override
    public void onResponse(CandlestickEvent e) {
        Integer symbolId = symbolMap.get(e.getSymbol());
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

        if(e.getBarFinal()){
            CandlestickService.addToQueue(candlestick);
        }
    }

    @Override
    public void onFailure(Throwable cause) {
        System.err.println("onFailure (candlestick): " + cause.toString());
    }
}
