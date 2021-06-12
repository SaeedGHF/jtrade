package com.jtradeplatform.saas.chart;

import com.jtradeplatform.saas.candlestick.Candlestick;

import java.util.List;

public abstract class BasePattern implements Runnable {

    protected List<Candlestick> candlesticks;
    protected PatternResultContainer resultContainer;

    public void setCandlesticks(List<Candlestick> candlesticks) {
        this.candlesticks = candlesticks;
    }

    public void setResultContainer(PatternResultContainer resultContainer) {
        this.resultContainer = resultContainer;
    }
}
