package com.jtradeplatform.saas.chart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtradeplatform.saas.chart.elements.TrendLineType;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.List;

public class ResultContainer {

    private final List<TrendLineType> trendLines;
    private final List<String> infoList;
    private final ObjectMapper objectMapper;

    ResultContainer() {
        this.objectMapper = new ObjectMapper();
        this.trendLines = new LinkedList<>();
        this.infoList = new LinkedList<>();
    }

    public synchronized void addLine(TrendLineType line) {
        trendLines.add(line);
    }

    public synchronized void addInfo(String info) {
        infoList.add(info);
    }

    public List<TrendLineType> getTrendLines() {
        return trendLines;
    }

    public List<String> getInfoList() {
        return infoList;
    }

    @SneakyThrows
    @Override
    public String toString() {
        return objectMapper.writeValueAsString(this);
    }
}
