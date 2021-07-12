package com.jtradeplatform.saas.chart;

public class PriceHelper {
    public static double calcDiffPercent(double from, double to) {
        return (from - to) / from * 100;
    }

    public static double calcDiffPercentAbs(double from, double to) {
        return Math.abs(PriceHelper.calcDiffPercent(from, to));
    }
}
