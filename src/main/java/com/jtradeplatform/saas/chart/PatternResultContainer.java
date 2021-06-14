package com.jtradeplatform.saas.chart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtradeplatform.saas.chart.elements.LineType;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Getter
public class PatternResultContainer {

    private final Map<Double, LineType> lines;
    private final List<String> infoList;

    PatternResultContainer() {
        this.lines = new HashMap<>();
        this.infoList = new LinkedList<>();
    }

    public synchronized void addLine(Double price, LineType lineType) {
        lines.put(price, lineType);
    }

    public synchronized void addInfo(String info) {
        infoList.add(info);
    }

    public boolean isEmpty() {
        return this.lines.isEmpty() && this.infoList.isEmpty();
    }

    @SneakyThrows
    @Override
    public String toString() {
        return (new ObjectMapper()).writeValueAsString(this);
    }
}
