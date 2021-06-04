package com.jtradeplatform.saas.landing;

import com.jtradeplatform.saas.candlestick.CandlestickService;
import com.jtradeplatform.saas.symbol.SymbolRepository;
import com.jtradeplatform.saas.symbol.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private SymbolRepository repo;
    @Autowired
    CandlestickService candlestickService;
    @Autowired
    SymbolService symbolService;
    //@Autowired
    //private CandlestickRepository cdRepo;

    @GetMapping
    public String main() {
        return "index";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/test")
    public String test() throws InterruptedException {
        candlestickService.test();
        return "faq";
    }

    @GetMapping("/clear")
    public String clear() {
        candlestickService.deleteAll();
        return "faq";
    }

    @GetMapping("/binance")
    public String binance() {
        symbolService.refreshAll();
        return "faq";
    }
}
