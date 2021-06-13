package com.jtradeplatform.saas.chart.patternsImpl;

import com.jtradeplatform.saas.candlestick.Candlestick;
import com.jtradeplatform.saas.chart.BasePattern;
import com.jtradeplatform.saas.chart.elements.TrendLineType;

public class MaxMinPattern extends BasePattern {

    private final double MIN_PERCENT_DISTANCE = 10.8;

    @Override
    public void run() {

        if (this.candlesticks.size() < 500) {
            return;
        }

        double maxPrice = 0;
        double minPrice = 0;
        double currentPrice = this.candlesticks.get(this.candlesticks.size() - 1).getClose();

        for (Candlestick candlestick : this.candlesticks) {
            maxPrice = candlestick.getHigh() > maxPrice ? candlestick.getHigh() : maxPrice;
            minPrice = candlestick.getLow() < minPrice || minPrice == 0 ? candlestick.getLow() : minPrice;
        }

        if (maxPrice > 0 && this.checkDistance(currentPrice, maxPrice)) {
            TrendLineType maxLine = TrendLineType.RESISTANCE_LINE;
            maxLine.setPrice(maxPrice);
            this.resultContainer.addLine(maxLine);
        }

        if (minPrice > 0 && this.checkDistance(currentPrice, minPrice)) {
            TrendLineType minLine = TrendLineType.SUPPORT_LINE;
            minLine.setPrice(minPrice);
            this.resultContainer.addLine(minLine);
        }
    }

    private boolean checkDistance(double currentPrice, double historicalPrice) {
        if (currentPrice == historicalPrice) {
            return false;
        }
        double percent = Math.abs((currentPrice - historicalPrice) / currentPrice * 100);
        return percent <= this.MIN_PERCENT_DISTANCE;
    }
}
