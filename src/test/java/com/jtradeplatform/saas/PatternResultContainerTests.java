package com.jtradeplatform.saas;

import com.jtradeplatform.saas.chart.PatternResultContainer;
import com.jtradeplatform.saas.chart.elements.LineType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class PatternResultContainerTests {

    PatternResultContainer resultContainer = new PatternResultContainer();

    @Test
    public void find() {
        resultContainer.reset(true, true);
        assertThrows(NullPointerException.class, () -> {
            resultContainer.putViewText("test", "text");
            resultContainer.putViewLine(0.4435, LineType.CASCADE_LINE);
        });
        resultContainer.setSignal(true, 0.234);
        resultContainer.putViewText("test0", "text0");
        resultContainer.putViewLine(0.4435, LineType.CASCADE_LINE);
        resultContainer.putViewText("test1", "text1");
        resultContainer.putViewLine(0.3435, LineType.VOLUME_LINE);
        System.out.println(resultContainer);
    }

    @Test
    public void findSignal() {
        resultContainer.reset(true, false);
        assertThrows(RuntimeException.class, () -> {
            resultContainer.setSignal(true, 0.234);
        });
        resultContainer.reset(true, false);
        assertThrows(RuntimeException.class, () -> {
            resultContainer.setSignal(false, 1.234);
        });
    }

    @Test
    public void findView() {
        resultContainer.reset(false, true);
        resultContainer.setSignal(false, 1.234);
        resultContainer.putViewText("test", "text");
        resultContainer.putViewLine(0.4435, LineType.CASCADE_LINE);
        resultContainer.putViewText("test3", "text3");
        resultContainer.putViewLine(0.5435, LineType.HISTORICAL_MAX);
    }
}
