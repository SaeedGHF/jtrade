package com.jtradeplatform.saas.candlestick;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "candlestick")
public class Candlestick {

    @Column(timestamp = true)
    Instant time;

    @Column(tag = true)
    String period;

    @Column(tag = true)
    String symbol;

    @Column
    Double open;

    @Column
    Double high;

    @Column
    Double low;

    @Column
    Double close;

    public Candlestick(){

    }

    public Candlestick(Instant time, String period, String symbol, Double open, Double high, Double low, Double close) {
        this.time = time;
        this.period = period;
        this.symbol = symbol;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    @Override
    public String toString() {
        return "Candlestick{" +
                "time=" + time +
                ", period='" + period + '\'' +
                ", symbol=" + symbol +
                '}';
    }
}
