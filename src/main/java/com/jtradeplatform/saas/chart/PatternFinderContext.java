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
        Map<String, Thread> threadList = new HashMap<>();

        for (val ptClass : this.patternClasses) {
            try {
                BasePattern pt = ptClass.newInstance();
                pt.setCandlesticks(candlestickList);
                pt.setResultContainer(resultContainer);
                Thread patternThread = new Thread(pt);
                patternThread.setName(ptClass.toString());
                patternThread.start();
                threadList.put(patternThread.getName(), patternThread);
            } catch (InstantiationException | IllegalAccessException e) {
                System.err.println("Pattern error" + e);
            }
        }

        for (Map.Entry<String, Thread> entry : threadList.entrySet()) {
            try {
                entry.getValue().join();
            } catch (InterruptedException e) {
                System.err.println("Thread error: " + e);
            }
        }
        return resultContainer;
    }
}
