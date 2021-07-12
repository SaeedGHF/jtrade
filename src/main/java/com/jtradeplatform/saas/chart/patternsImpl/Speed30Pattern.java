package com.jtradeplatform.saas.chart.patternsImpl;

import com.jtradeplatform.saas.candlestick.Candlestick;
import com.jtradeplatform.saas.chart.AbstractPattern;
import com.jtradeplatform.saas.chart.PriceHelper;

public class Speed30Pattern extends AbstractPattern {

    protected void execute() {
        double minPercent = 15;
        double percentChange = getPricePercentChange();
        boolean signal = percentChange > minPercent;
        resultContainer.setSignal(signal, percentChange);
        resultContainer.putViewText("speed", "За 30 мин изменение цены " + percentChange + "%");
    }

    private double getPricePercentChange() {
        double percent = 0;
        if (this.candlesticks.size() < 30) {
            return percent;
        }
        Candlestick first = this.candlesticks.get(0);
        Candlestick last = this.candlesticks.get(29);
        double basePrice = first.getClose(),
                anotherPrice = first.getClose() > last.getLow() ? last.getLow() : last.getHigh();
        return PriceHelper.calcDiffPercentAbs(basePrice, anotherPrice);
    }
}
