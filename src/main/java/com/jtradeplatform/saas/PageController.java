package com.jtradeplatform.saas;

import com.jtradeplatform.saas.candlestick.CandlestickRepository;
import com.jtradeplatform.saas.candlestick.CandlestickService;
import com.jtradeplatform.saas.symbol.SymbolRepository;
import com.jtradeplatform.saas.symbol.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    private CandlestickRepository repo;
    @Autowired
    CandlestickService candlestickService;
    @Autowired
    SymbolService symbolService;

    @GetMapping
    public String main() {
        return "index";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/clear")
    public String clear() {
        candlestickService.deleteAll();
        return "faq";
    }

    @GetMapping("/symbol")
    public String symbol() {
        symbolService.refreshAll();
        return "faq";
    }

    @GetMapping("/binance")
    public String binance() {
        candlestickService.stopWatcher();
        candlestickService.runWatcher();
        return "faq";
    }
}
