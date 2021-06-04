package com.jtradeplatform.saas.services;

import com.jtradeplatform.saas.configs.BinanceApiConfig;
import org.springframework.stereotype.Service;

@Service
public final class BinanceSpot {

    BinanceApiConfig config;

    BinanceSpot(BinanceApiConfig binanceApiConfig) {
        config = binanceApiConfig;
    }
}
