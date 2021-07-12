package com.jtradeplatform.saas.chart;

import com.jtradeplatform.saas.candlestick.Candlestick;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PatternFinderContext {

    List<Class<? extends AbstractPattern>> patternClasses;

    public void setPatternClasses(List<Class<? extends AbstractPattern>> patternClasses) {
        this.patternClasses = patternClasses;
    }

    public List<PatternResultContainer> find(List<Candlestick> reversedCandlestickList) {
        List<PatternResultContainer> resultList = new LinkedList<>();
        for (val ptClass : this.patternClasses) {
            AbstractPattern pt = createPatternInstance(ptClass);
            if (pt == null) continue;
            pt.setCandlesticks(reversedCandlestickList);
            resultList.add(pt.findSignal());
        }
        return resultList;
    }

    private AbstractPattern createPatternInstance(Class<? extends AbstractPattern> patternClass) {
        try {
            return patternClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println("Pattern error:" + e);
            return null;
        }
    }
}
