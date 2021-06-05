package com.jtradeplatform.saas.candlestick;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "candlestick")
public class Candlestick {

    public Candlestick(Instant time, String period, Integer symbol, Double open, Double high, Double low, Double close) {
        this.time = time;
        this.period = period;
        this.symbol = symbol;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    @Column(timestamp = true)
    Instant time;

    @Column(tag = true)
    String period;

    @Column(tag = true)
    Integer symbol;

    @Column
    Double open;

    @Column
    Double high;

    @Column
    Double low;

    @Column
    Double close;
}
