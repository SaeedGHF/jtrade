package com.jtradeplatform.saas.chart;

import com.jtradeplatform.saas.candlestick.Candlestick;
import lombok.Setter;

import java.util.List;

public abstract class AbstractPattern {

    @Setter
    protected List<Candlestick> candlesticks;
    protected PatternResultContainer resultContainer = new PatternResultContainer();

    protected abstract void execute();

    public PatternResultContainer find() {
        resultContainer.reset(true, true);
        executeHandleException();
        return resultContainer;
    }

    public PatternResultContainer findSignal() {
        resultContainer.reset(true, false);
        executeHandleException();
        return resultContainer;
    }

    public PatternResultContainer findView() {
        resultContainer.reset(false, true);
        executeHandleException();
        return resultContainer;
    }

    private void executeHandleException() {
        try {
            execute();
        } catch (RuntimeException e) {
            System.err.println(e);
        }
    }
}
