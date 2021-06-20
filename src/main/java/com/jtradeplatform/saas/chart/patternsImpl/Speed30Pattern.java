package com.jtradeplatform.saas.chart.patternsImpl;

import com.jtradeplatform.saas.candlestick.Candlestick;
import com.jtradeplatform.saas.chart.BasePattern;
import com.jtradeplatform.saas.chart.PriceHelper;

public class Speed30Pattern extends BasePattern {

    @Override
    public void run() {
        if (this.candlesticks.size() < 30) {
            return;
        }

        Candlestick first = this.candlesticks.get(0);
        Candlestick last = this.candlesticks.get(30);
        double percent = 0;
        double minPercent = 15;
        if (first.getClose() > last.getLow()) {
            percent = Math.abs(PriceHelper.calcDiffPercent(first.getClose(), last.getLow()));
        } else {
            percent = Math.abs(PriceHelper.calcDiffPercent(first.getClose(), last.getHigh()));
        }

        if (percent > minPercent) {
            this.resultContainer.addInfo("speed", "За 30 мин изменение цены " + percent + "%");
        }

    }
}
