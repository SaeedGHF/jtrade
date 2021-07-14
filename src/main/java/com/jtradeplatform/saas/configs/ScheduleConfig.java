package com.jtradeplatform.saas.configs;

import com.jtradeplatform.saas.candlestick.CandlestickService;
import com.jtradeplatform.saas.event.EventService;
import com.jtradeplatform.saas.market.candlesticks.CandlestickQueue;
import com.jtradeplatform.saas.market.candlesticks.CandlestickWatcher;
import com.jtradeplatform.saas.symbol.SymbolService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Instant;


@Configuration
@EnableAsync
@EnableScheduling
@AllArgsConstructor
public class ScheduleConfig {

    SymbolService symbolService;
    CandlestickQueue candlestickQueue;
    CandlestickWatcher candlestickWatcher;
    CandlestickService candlestickService;
    EventService eventService;

    @PostConstruct
    private void postConstruct() throws IOException {
        candlestickQueue.disableSavingCandlesticks();
        candlestickWatcher.run();
        System.out.println("Start charts updating at " + Instant.now().toString());
        candlestickService.updateAllCharts();
        System.out.println("Charts updated at " + Instant.now().toString());
        candlestickQueue.enableSavingCandlesticks();
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void refreshSymbolList() {
        symbolService.refreshAll();
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void runCandlestickQueue() {
        candlestickQueue.run();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void removeOldEvents() {
        eventService.removeOldEvents();
    }

    @Scheduled(cron = "*/60 * * * * *")
    public void runPatternFinder() {
        long startTime = System.currentTimeMillis();
        System.out.println("RUN runPatternFinder at " + Instant.now().toString());
        candlestickService.findPatternsAndSend();
        long endTime = System.currentTimeMillis();
        System.out.println("STOP runPatternFinder execution time: " + (endTime - startTime) + "ms");
    }
}
