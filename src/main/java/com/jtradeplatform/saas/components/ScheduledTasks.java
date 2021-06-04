package com.jtradeplatform.saas.components;

import com.jtradeplatform.saas.symbol.SymbolService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    SymbolService symbolService;

    ScheduledTasks(SymbolService symbolService) {
        this.symbolService = symbolService;
        //this.refreshSymbolList();
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void refreshSymbolList() {
        symbolService.refreshAll();
    }
}