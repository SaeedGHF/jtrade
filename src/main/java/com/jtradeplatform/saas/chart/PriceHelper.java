package com.jtradeplatform.saas.chart;

public class PriceHelper {
    public static double calcDiffPercent(double targetPrice, double anotherPrice) {
        return (targetPrice - anotherPrice) / targetPrice * 100;
    }
}
