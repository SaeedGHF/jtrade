package com.jtradeplatform.saas.configs;

import com.jtradeplatform.saas.candlestick.CandlestickService;
import com.jtradeplatform.saas.symbol.SymbolService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;


@Configuration
@EnableAsync
@EnableScheduling
@AllArgsConstructor
public class ScheduleConfig {

    SymbolService symbolService;
    CandlestickService candlestickService;

    @Scheduled(cron = "0 0 6 * * *")
    public void refreshSymbolList() {
        symbolService.refreshAll();
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void runCandlestickQueue() {
        candlestickService.runQueue();
    }

    @Scheduled(cron = "*/60 * * * * *")
    public void runPatternFinder() {
        long startTime = System.currentTimeMillis();
        System.out.println("RUN runPatternFinder at " + Instant.now().toString());
        candlestickService.findPatternsAndSend();
        long endTime = System.currentTimeMillis();
        System.out.println("STOP runPatternFinder execution time: " + (endTime-startTime) + "ms");
    }
}
