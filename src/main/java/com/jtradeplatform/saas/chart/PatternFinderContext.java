package com.jtradeplatform.saas.chart;

import com.jtradeplatform.saas.candlestick.Candlestick;
import com.jtradeplatform.saas.chart.patternsImpl.Cascade;
import com.jtradeplatform.saas.chart.patternsImpl.Speed30;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PatternFinderContext {

    List<Class<? extends PatternDefault>> patternClasses = Arrays.asList(
            Speed30.class,
            Cascade.class
    );

    public void setPatternClasses(List<Class<? extends PatternDefault>> patternClasses) {
        this.patternClasses = patternClasses;
    }

    public ResultContainer find(List<Candlestick> candlestickList) throws InstantiationException, IllegalAccessException, InterruptedException {
        ResultContainer resultContainer = new ResultContainer();
        Map<String, Thread> threadList = new HashMap<>();

        for (Class<? extends PatternDefault> ptClass : this.patternClasses) {
            PatternDefault pt = ptClass.newInstance();
            pt.setCandlesticks(candlestickList);
            pt.setResultContainer(resultContainer);
            Thread patternThread = new Thread(pt);
            patternThread.setName(ptClass.toString());
            patternThread.start();
            threadList.put(patternThread.getName(), patternThread);
        }

        for (Map.Entry<String, Thread> entry : threadList.entrySet()) {
            entry.getValue().join();
        }

        return resultContainer;
    }
}
