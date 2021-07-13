package com.jtradeplatform.saas.chart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtradeplatform.saas.chart.elements.LineType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public final class PatternResultContainer {

    private SignalData signalData = new SignalData();
    private ViewData viewData = new ViewData();
    private boolean enableSignal = false, enableView = false;

    public void reset(boolean enableSignal, boolean enableView) {
        this.enableView = enableView;
        this.enableSignal = enableSignal;
        signalData = new SignalData();
        viewData = new ViewData();
    }

    public void setSignal(boolean signal, double value) {
        checkSignalRule();
        signalData.setSignal(signal);
        signalData.setValue(value);
        checkViewRule();
    }

    public void putViewLine(Double price, LineType lineType) {
        checkViewRule();
        viewData.lines.put(price, lineType);
    }

    public void putViewText(String title, String description) {
        checkViewRule();
        viewData.texts.put(title, description);
    }

    private void checkViewRule() {
        if (!enableView) {
            throw new RuntimeException("`View` is disabled in PatternResultContainer");
        }
        signalData.signalRequiredNonNull();
    }

    public boolean isSignalExists() {
        return signalData.getSignal() != null;
    }

    public boolean getSignal() {
        return signalData.getSignal();
    }

    private void checkSignalRule() {
        if (enableSignal && signalData.getSignal() != null) {
            throw new RuntimeException("`Signal` already exists");
        }
    }

    @Getter
    public static class ViewData {
        private final Map<Double, LineType> lines = new HashMap<>();
        private final Map<String, String> texts = new HashMap<>();
    }

    @Getter
    @Setter
    public static class SignalData {
        private Boolean signal;
        private Double value;
        private final String signalNullErrMsg = "Signal must not be null";

        private void signalRequiredNonNull() {
            Objects.requireNonNull(signal, signalNullErrMsg);
        }

        public void setSignal(Boolean signal) {
            if (this.signal != null) {
                throw new RuntimeException("Signal already set");
            }
            Objects.requireNonNull(signal, signalNullErrMsg);
            this.signal = signal;
        }

        @Override
        public String toString() {
            return "SignalData{" +
                    "signal=" + signal +
                    ", value=" + value +
                    '}';
        }
    }


    @SneakyThrows
    @Override
    public String toString() {
        return (new ObjectMapper()).writeValueAsString(this);
    }
}
