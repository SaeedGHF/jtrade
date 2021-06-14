package com.jtradeplatform.saas.chart.elements;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LineType {

    SUPPORT_LINE("Поддержка", "green", "Solid"),
    RESISTANCE_LINE("Сопротивление", "red", "Solid"),
    CASCADE_LINE("Каскад", "orange", "Dotted"),
    ICEBERG_LINE("Айсберг", "blue", "SparseDotted"),
    VOLUME_LINE("Объем", "black", "SparseDotted"),
    HISTORICAL_MIN("Ист. минимум", "yellow", "LargeDashed"),
    HISTORICAL_MAX("Ист. максимум", "yellow", "LargeDashed");

    private final String name;
    private final String color;
    private final String style;

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getStyle() {
        return style;
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    LineType(String name, String color, String style) {
        this.name = name;
        this.color = color;
        this.style = style;
    }

    @SneakyThrows
    @Override
    public String toString() {
        return objectMapper.writeValueAsString(this);
    }
}
