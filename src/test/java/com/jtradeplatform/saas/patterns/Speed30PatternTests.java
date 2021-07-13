package com.jtradeplatform.saas.patterns;

import com.jtradeplatform.saas.CandlestickLoader;
import com.jtradeplatform.saas.chart.PatternResultContainer;
import com.jtradeplatform.saas.chart.patternsImpl.Speed30Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class Speed30PatternTests {

    private final double SIGNAL_VALUE = 0.1332991308546543D;
    private final boolean SIGNAL = false;
    private final String VIEW_TEXT_KEY = "speed";

    private Speed30Pattern getPattern() throws FileNotFoundException {
        Speed30Pattern pattern = new Speed30Pattern();
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
        assertNotNull(viewData.getTexts().get(VIEW_TEXT_KEY));
    }

    @Test
    public void findSignalTest() throws FileNotFoundException {
        PatternResultContainer resultContainer = getPattern().findSignal();
        PatternResultContainer.SignalData signalData = resultContainer.getSignalData();
        PatternResultContainer.ViewData viewData = resultContainer.getViewData();
        assertEquals(signalData.getValue(), SIGNAL_VALUE);
        assertEquals(signalData.getSignal(), SIGNAL);
        assertNull(viewData.getTexts().get(VIEW_TEXT_KEY));
    }

    @Test
    public void findViewTest() throws FileNotFoundException {
        PatternResultContainer resultContainer = getPattern().findView();
        PatternResultContainer.SignalData signalData = resultContainer.getSignalData();
        PatternResultContainer.ViewData viewData = resultContainer.getViewData();
        assertEquals(signalData.getValue(), SIGNAL_VALUE);
        assertEquals(signalData.getSignal(), SIGNAL);
        assertNotNull(viewData.getTexts().get(VIEW_TEXT_KEY));
    }
}
