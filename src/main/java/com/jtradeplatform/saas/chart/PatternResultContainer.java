package com.jtradeplatform.saas.chart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtradeplatform.saas.chart.elements.TrendLineType;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.List;


@Getter
public class PatternResultContainer {

    private final List<TrendLineType> trendLines;
    private final List<String> infoList;

    PatternResultContainer() {
        this.trendLines = new LinkedList<>();
        this.infoList = new LinkedList<>();
    }

    public synchronized void addLine(TrendLineType line) {
        trendLines.add(line);
    }

    public synchronized void addInfo(String info) {
        infoList.add(info);
    }

    public boolean isEmpty(){
        return true;
    }

    @SneakyThrows
    @Override
    public String toString() {
        return (new ObjectMapper()).writeValueAsString(this);
    }
}
