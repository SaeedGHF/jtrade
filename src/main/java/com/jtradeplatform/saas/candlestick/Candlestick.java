package com.jtradeplatform.saas.candlestick;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Getter;

import java.time.Instant;

@Measurement(name = "candlestick")
@Getter
public class Candlestick {

    private static final String MEASUREMENT_NAME = "candlestick";

    @Column(timestamp = true)
    Instant time;

    @Column(tag = true)
    String period;

    @Column(tag = true)
    String symbol;

    @Column
    String value;

    //
    private Double open;
    private Double high;
    private Double low;
    private Double close;

    public Candlestick() {

    }

    public Candlestick(Instant time, String period, String symbol, Double open, Double high, Double low, Double close) {
        this.time = time;
        this.period = period;
        this.symbol = symbol;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.convertToValue();
    }

    private void convertToValue() {
        this.value = open + ":" + high + ":" + low + ":" + close;
    }

    public void parseValue() {
        String[] list = this.value.split(":");
        if (list.length != 4) {
            throw new RuntimeException("Failed to check candlestick list length");
        }

        open = Double.parseDouble(list[0]);
        high = Double.parseDouble(list[1]);
        low = Double.parseDouble(list[2]);
        close = Double.parseDouble(list[3]);
    }

    public static String getMeasureName() {
        return MEASUREMENT_NAME;
    }

    @Override
    public String toString() {
        return String.format("%s;%s;%s;%s;%s;%s", symbol, time, open, high, low, close);
    }
}
