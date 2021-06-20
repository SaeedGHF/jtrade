package com.jtradeplatform.saas.chart.patternsImpl;

import com.jtradeplatform.saas.candlestick.Candlestick;
import com.jtradeplatform.saas.chart.BasePattern;
import com.jtradeplatform.saas.chart.PriceHelper;
import com.jtradeplatform.saas.chart.elements.LineType;

import java.util.*;

public class CascadePattern extends BasePattern {

    private final double MAX_ACTIVATE_DISTANCE = 0.5d;
    private final int MIN_HISTORY_SIZE = 500;
    private final int MAX_PRICE_POINTS = 200;
    private final int MAX_DISPLAY_LINES = 12;
    private final int GRAY_AREA = 15;
    private final double MIN_CHANGE_PERCENT = 1.2;
    private final String TYPE_TOP = "top";
    private final String TYPE_BOTTOM = "bottom";
    private final ArrayDeque<PricePoint> pricePoints = new ArrayDeque<>(MAX_PRICE_POINTS);
    private double greyMaxPrice = 0;
    private double greyMinPrice = 0;

    @Override
    public void run() {

        if (this.candlesticks.size() < MIN_HISTORY_SIZE) {
            return;
        }

        Candlestick first = this.candlesticks.get(0);
        if (first == null) {
            return;
        }
        //
        double currentPrice = first.getClose();
        int candlestickCounter = 0;
        PricePoint lastPricePoint;

        for (Candlestick c : this.candlesticks) {
            candlestickCounter++;

            if (candlestickCounter <= GRAY_AREA) {
                greyMaxPrice = c.getHigh() > greyMaxPrice ? c.getHigh() : greyMaxPrice;
                greyMinPrice = c.getLow() < greyMinPrice || greyMinPrice == 0 ? c.getLow() : greyMinPrice;
                continue;
            }

            // find first type
            if (pricePoints.isEmpty()) {
                if (PriceHelper.calcDiffPercent(currentPrice, c.getHigh()) < -0.25 && c.getHigh() > greyMaxPrice) {
                    pricePoints.add(new PricePoint(c.getHigh(), TYPE_TOP));
                }
                if (PriceHelper.calcDiffPercent(currentPrice, c.getLow()) > 0.25 && c.getLow() < greyMinPrice) {
                    pricePoints.add(new PricePoint(c.getLow(), TYPE_BOTTOM));
                }
                continue;
            }

            // check event rule
            if (pricePoints.size() == 2 && !checkActivateDistance(currentPrice, pricePoints.getFirst().price)) {
                return;
            }

            if (pricePoints.size() > MAX_PRICE_POINTS) {
                break;
            }

            lastPricePoint = pricePoints.getLast();


            // higher
            if (lastPricePoint.type.equals(TYPE_TOP) && lastPricePoint.price < c.getHigh()) {
                lastPricePoint.setPrice(c.getHigh());
                continue;
            }
            // lower
            if (lastPricePoint.type.equals(TYPE_BOTTOM) && lastPricePoint.price > c.getLow()) {
                lastPricePoint.setPrice(c.getLow());
                continue;
            }

            // if type exists, check change type and update step
            if (lastPricePoint.type.equals(TYPE_BOTTOM) && PriceHelper.calcDiffPercent(lastPricePoint.price, c.getHigh()) < -MIN_CHANGE_PERCENT) {
                pricePoints.add(new PricePoint(c.getHigh(), TYPE_TOP));
            }
            if (lastPricePoint.type.equals(TYPE_TOP) && PriceHelper.calcDiffPercent(lastPricePoint.price, c.getLow()) > MIN_CHANGE_PERCENT) {
                pricePoints.add(new PricePoint(c.getLow(), TYPE_BOTTOM));
            }
        }

        this.convertPricePoints();
    }

    private void convertPricePoints() {
        double lastTopPrice = 0, lastBottomPrice = 0;
        int countLines = 0;

        for (PricePoint p : pricePoints) {
            if (countLines >= MAX_DISPLAY_LINES) {
                break;
            }
            if (p.type.equals(TYPE_TOP)) {
                if ((lastTopPrice == 0 || p.price > lastTopPrice) && p.price > greyMaxPrice) {
                    addResistanceLine(p.price);
                    lastTopPrice = p.price;
                    countLines++;
                }
            }
            if (p.type.equals(TYPE_BOTTOM)) {
                if ((lastBottomPrice == 0 || p.price < lastBottomPrice) && p.price < greyMinPrice) {
                    addSupportLine(p.price);
                    lastBottomPrice = p.price;
                    countLines++;
                }
            }
        }
    }

    private boolean checkActivateDistance(double targetPrice, double anotherPrice) {
        if (targetPrice == anotherPrice) {
            return false;
        }
        double percent = Math.abs(PriceHelper.calcDiffPercent(targetPrice, anotherPrice));
        return percent <= this.MAX_ACTIVATE_DISTANCE;
    }

    private void addResistanceLine(double price) {
        this.addLine(price, LineType.RESISTANCE_LINE);
    }

    private void addSupportLine(double price) {
        this.addLine(price, LineType.SUPPORT_LINE);
    }

    private void addLine(double price, LineType line) {
        this.resultContainer.addLine(price, line);
    }

    public int getMAX_DISPLAY_LINES() {
        return MAX_DISPLAY_LINES;
    }

    public double getMAX_ACTIVATE_DISTANCE() {
        return MAX_ACTIVATE_DISTANCE;
    }

    public int getMIN_HISTORY_SIZE() {
        return MIN_HISTORY_SIZE;
    }

    public int getGRAY_AREA() {
        return GRAY_AREA;
    }

    public double getMIN_CHANGE_PERCENT() {
        return MIN_CHANGE_PERCENT;
    }

    private static class PricePoint {
        private double price;
        private String type;

        public PricePoint(double price, String type) {
            this.price = price;
            this.type = type;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getStep() {
            return type;
        }

        public void setStep(String type) {
            this.type = type;
        }
    }
}
