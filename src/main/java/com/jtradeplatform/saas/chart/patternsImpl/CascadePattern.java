package com.jtradeplatform.saas.chart.patternsImpl;

import com.jtradeplatform.saas.candlestick.Candlestick;
import com.jtradeplatform.saas.chart.AbstractPattern;
import com.jtradeplatform.saas.chart.PriceHelper;
import com.jtradeplatform.saas.chart.elements.LineType;

import java.util.*;

public class CascadePattern extends AbstractPattern {

    private final int MAX_PRICE_POINTS = 200;
    private final ArrayDeque<PricePoint> pricePoints = new ArrayDeque<>(MAX_PRICE_POINTS);
    private double greyZoneMaxPrice = 0;
    private double greyZoneMinPrice = 0;
    private double lastPrice = 0;
    private Candlestick currCandlestick;
    private int candlestickCounter = 0;

    protected void execute() {
        int MIN_HISTORY_SIZE = 500;
        if (this.candlesticks.size() < MIN_HISTORY_SIZE) return;
        setLastPrice();

        for (Candlestick c : this.candlesticks) {
            setCurrCandlestick(c);
            if (isGrayZone()) {
                updateGrayZonePivots();
                continue;
            }
            if (pricePoints.isEmpty()) {
                findAndSetFirstPricePoint();
                continue;
            }
            setSignal();
            if (pricePoints.size() > MAX_PRICE_POINTS) break;
            addOrUpdatePricePoint();
        }

        convertPricePointsToLines();
    }

    private void addOrUpdatePricePoint() {
        PricePoint lastPricePoint = pricePoints.getLast();
        // update price point (lower/higher)
        if (lastPricePoint.isTop() && lastPricePoint.price < currCandlestick.getHigh()) {
            lastPricePoint.setPrice(currCandlestick.getHigh());
            return;
        }
        if (lastPricePoint.isBottom() && lastPricePoint.price > currCandlestick.getLow()) {
            lastPricePoint.setPrice(currCandlestick.getLow());
            return;
        }
        // change pivot type
        double MIN_CHANGE_PERCENT = 1.2;
        if (lastPricePoint.isBottom() && PriceHelper.calcDiffPercent(lastPricePoint.price, currCandlestick.getHigh()) < -MIN_CHANGE_PERCENT) {
            pricePoints.add(new PricePoint(currCandlestick.getHigh(), PricePointType.TOP));
        }
        if (lastPricePoint.isTop() && PriceHelper.calcDiffPercent(lastPricePoint.price, currCandlestick.getLow()) > MIN_CHANGE_PERCENT) {
            pricePoints.add(new PricePoint(currCandlestick.getLow(), PricePointType.BOTTOM));
        }
    }

    private void setSignal() {
        if (pricePoints.size() == 2) {
            double percent = calcChangePercent(lastPrice, pricePoints.getFirst().price);
            resultContainer.setSignal(isValidActivationDistance(percent), percent);
        }
    }

    private boolean isGrayZone() {
        int GRAY_ZONE = 15;
        return candlestickCounter <= GRAY_ZONE;
    }

    private void updateGrayZonePivots() {
        greyZoneMaxPrice = currCandlestick.getHigh() > greyZoneMaxPrice ? currCandlestick.getHigh() : greyZoneMaxPrice;
        greyZoneMinPrice = currCandlestick.getLow() < greyZoneMinPrice || greyZoneMinPrice == 0 ? currCandlestick.getLow() : greyZoneMinPrice;
    }

    private void setCurrCandlestick(Candlestick c) {
        currCandlestick = c;
        candlestickCounter++;
    }

    private void setLastPrice() {
        Candlestick first = this.candlesticks.get(0);
        if (first == null) {
            return;
        }
        lastPrice = first.getClose();
    }

    private void findAndSetFirstPricePoint() {
        PricePoint pricePoint = null;
        if (PriceHelper.calcDiffPercent(lastPrice, currCandlestick.getHigh()) < -0.20 && currCandlestick.getHigh() > greyZoneMaxPrice) {
            pricePoint = new PricePoint(currCandlestick.getHigh(), PricePointType.TOP);
        }
        if (PriceHelper.calcDiffPercent(lastPrice, currCandlestick.getLow()) > 0.20 && currCandlestick.getLow() < greyZoneMinPrice) {
            pricePoint = new PricePoint(currCandlestick.getLow(), PricePointType.BOTTOM);
        }

        if (pricePoint != null) {
            pricePoints.add(pricePoint);
        }
    }

    private void convertPricePointsToLines() {
        double lastTopPrice = 0, lastBottomPrice = 0;
        int countLines = 0, MAX_DISPLAY_LINES = 12;

        for (PricePoint p : pricePoints) {
            if (countLines >= MAX_DISPLAY_LINES) break;
            if (p.isTop() && (lastTopPrice == 0 || p.price > lastTopPrice) && p.price > greyZoneMaxPrice) {
                addResistanceLine(p.price);
                lastTopPrice = p.price;
                countLines++;
            }
            if (p.isBottom() && (lastBottomPrice == 0 || p.price < lastBottomPrice) && p.price < greyZoneMinPrice) {
                addSupportLine(p.price);
                lastBottomPrice = p.price;
                countLines++;
            }
        }
    }

    private boolean isValidActivationDistance(double percent) {
        double MAX_ACTIVATE_DISTANCE = 0.5d;
        return percent <= MAX_ACTIVATE_DISTANCE;
    }

    private double calcChangePercent(double targetPrice, double anotherPrice) {
        return Math.abs(PriceHelper.calcDiffPercent(targetPrice, anotherPrice));
    }

    private void addResistanceLine(double price) {
        this.addLine(price, LineType.RESISTANCE_LINE);
    }

    private void addSupportLine(double price) {
        this.addLine(price, LineType.SUPPORT_LINE);
    }

    private void addLine(double price, LineType line) {
        this.resultContainer.putViewLine(price, line);
    }

    private static class PricePoint {
        private double price;
        private final PricePointType type;

        public boolean isTop() {
            return type.equals(PricePointType.TOP);
        }

        public boolean isBottom() {
            return type.equals(PricePointType.BOTTOM);
        }

        public PricePoint(double price, PricePointType type) {
            this.price = price;
            this.type = type;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

    private enum PricePointType {
        TOP, BOTTOM
    }
}
