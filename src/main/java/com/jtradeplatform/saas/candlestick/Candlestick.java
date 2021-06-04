package com.jtradeplatform.saas.candlestick;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "candlestick")
public class Candlestick {

    public Candlestick() {

    }

    public Candlestick(Instant time, Short period, Integer currencyPair, Double open, Double high, Double low, Double close) {
        this.time = time;
        this.period = period;
        this.currencyPair = currencyPair;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    @Column(timestamp = true)
    Instant time;

    @Column(tag = true)
    Short period;

    @Column(tag = true)
    Integer currencyPair;

    @Column
    Double open;

    @Column
    Double high;

    @Column
    Double low;

    @Column
    Double close;
}
