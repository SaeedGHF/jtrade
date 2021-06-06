package com.jtradeplatform.saas.configs;

import com.jtradeplatform.saas.candlestick.CandlestickService;
import com.jtradeplatform.saas.symbol.SymbolService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@EnableAsync
@EnableScheduling
public class ScheduleConfig {

    SymbolService symbolService;
    CandlestickService candlestickService;

    ScheduleConfig(SymbolService symbolService, CandlestickService candlestickService) {
        this.symbolService = symbolService;
        this.candlestickService = candlestickService;
        this.refreshSymbolList();
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void refreshSymbolList() {
        symbolService.refreshAll();
    }

    //
    @Scheduled(cron = "*/2 * * * * *")
    public void runCandlestickQueue() {
        candlestickService.runQueue();
    }
}
