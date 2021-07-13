package com.jtradeplatform.saas.configs;

import com.jtradeplatform.saas.candlestick.CandlestickService;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
public class BootstrapAppConfig {

    BootstrapAppConfig(CandlestickService candlestickService) {
        System.out.println("Start charts updating at " + Instant.now().toString());
        candlestickService.updateAllCharts();
        System.out.println("Charts updated at " + Instant.now().toString());
        candlestickService.runWatcher();
    }
}
