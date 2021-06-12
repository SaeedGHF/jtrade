package com.jtradeplatform.saas.configs;

import com.jtradeplatform.saas.candlestick.CandlestickService;
import com.jtradeplatform.saas.event.EventService;
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
    EventService eventService;

    @Scheduled(cron = "0 0 6 * * *")
    public void refreshSymbolList() {
        symbolService.refreshAll();
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void runCandlestickQueue() {
        candlestickService.runQueue();
    }

    //@Scheduled(cron = "*/20 * * * * *")
    //public void runPatternFinder() {
    //    System.out.println("RUN runPatternFinder at " + Instant.now().toString());
    //    eventService.findPatternsAndSend();
    //    System.out.println("STOP runPatternFinder at " + Instant.now().toString());
    //}
}
