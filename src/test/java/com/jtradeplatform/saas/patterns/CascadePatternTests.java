package com.jtradeplatform.saas.patterns;

import com.jtradeplatform.saas.CandlestickLoader;
import com.jtradeplatform.saas.chart.PatternResultContainer;
import com.jtradeplatform.saas.chart.patternsImpl.CascadePattern;
import com.jtradeplatform.saas.chart.patternsImpl.Speed30Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CascadePatternTests {

    @Test
    public void findTest() throws FileNotFoundException {
        CascadePattern pattern = new CascadePattern();
        pattern.setCandlesticks(CandlestickLoader.getReversedData());
        PatternResultContainer resultContainer = pattern.find();
        PatternResultContainer.SignalData signalData = resultContainer.getSignalData();
        PatternResultContainer.ViewData viewData = resultContainer.getViewData();
        assertEquals(signalData.getValue(), 0.1332991308546543D);
        assertEquals(signalData.getSignal(), false);
        assertNotNull(viewData.getTexts().get("speed"));
    }
}
