package com.jtradeplatform.saas.chart;

import com.jtradeplatform.saas.candlestick.Candlestick;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PatternFinderContext {

    List<Class<? extends BasePattern>> patternClasses;

    public void setPatternClasses(List<Class<? extends BasePattern>> patternClasses) {
        this.patternClasses = patternClasses;
    }

    public PatternResultContainer find(List<Candlestick> candlestickList) {
        PatternResultContainer resultContainer = new PatternResultContainer();

        for (val ptClass : this.patternClasses) {
            try {
                BasePattern pt = ptClass.newInstance();
                pt.setCandlesticks(candlestickList);
                pt.setResultContainer(resultContainer);
                pt.run();
            } catch (InstantiationException | IllegalAccessException e) {
                System.err.println("Pattern error" + e);
            }
        }
        return resultContainer;
    }
}
