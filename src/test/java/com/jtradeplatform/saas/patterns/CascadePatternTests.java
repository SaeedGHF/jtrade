package com.jtradeplatform.saas.patterns;

import com.jtradeplatform.saas.CandlestickLoader;
import com.jtradeplatform.saas.chart.PatternResultContainer;
import com.jtradeplatform.saas.chart.patternsImpl.CascadePattern;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CascadePatternTests {

    private final double SIGNAL_VALUE = 1.4428718010622905D;
    private final boolean SIGNAL = false;
    private final double LINE_PRICE = 33614.11D;

    private CascadePattern getPattern() throws FileNotFoundException {
        CascadePattern pattern = new CascadePattern();
        pattern.setCandlesticks(CandlestickLoader.getReversedData());
        return pattern;
    }

    @Test
    public void findTest() throws FileNotFoundException {
        PatternResultContainer resultContainer = getPattern().find();
        PatternResultContainer.SignalData signalData = resultContainer.getSignalData();
        PatternResultContainer.ViewData viewData = resultContainer.getViewData();
        assertEquals(signalData.getValue(), SIGNAL_VALUE);
        assertEquals(signalData.getSignal(), SIGNAL);
        assertEquals(viewData.getLines().size(), 1);
        assertTrue(viewData.getLines().containsKey(LINE_PRICE));
    }

    @Test
    public void findSignalTest() throws FileNotFoundException {
        PatternResultContainer resultContainer = getPattern().findSignal();
        PatternResultContainer.SignalData signalData = resultContainer.getSignalData();
        PatternResultContainer.ViewData viewData = resultContainer.getViewData();
        assertEquals(signalData.getValue(), SIGNAL_VALUE);
        assertEquals(signalData.getSignal(), SIGNAL);
        assertEquals(viewData.getLines().size(), 0);
        assertFalse(viewData.getLines().containsKey(LINE_PRICE));
    }

    @Test
    public void findViewTest() throws FileNotFoundException {
        PatternResultContainer resultContainer = getPattern().findView();
        PatternResultContainer.SignalData signalData = resultContainer.getSignalData();
        PatternResultContainer.ViewData viewData = resultContainer.getViewData();
        assertEquals(signalData.getValue(), SIGNAL_VALUE);
        assertEquals(signalData.getSignal(), SIGNAL);
        assertEquals(viewData.getLines().size(), 1);
        assertTrue(viewData.getLines().containsKey(LINE_PRICE));
    }
}
