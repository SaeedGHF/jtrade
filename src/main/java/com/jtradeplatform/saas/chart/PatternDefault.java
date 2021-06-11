package com.jtradeplatform.saas.chart;

import com.jtradeplatform.saas.candlestick.Candlestick;

import java.util.List;

public abstract class PatternDefault implements Runnable {

    protected List<Candlestick> candlesticks;
    protected ResultContainer resultContainer;

    public void setCandlesticks(List<Candlestick> candlesticks) {
        this.candlesticks = candlesticks;
    }

    public void setResultContainer(ResultContainer resultContainer) {
        this.resultContainer = resultContainer;
    }
}
