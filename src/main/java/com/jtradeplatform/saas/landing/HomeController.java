package com.jtradeplatform.saas.landing;

import com.jtradeplatform.saas.currencyPair.CurrencyPair;
import com.jtradeplatform.saas.currencyPair.CurrencyPairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private CurrencyPairRepository repo;

    @GetMapping
    public String main() {
        return "index";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/test")
    public String test() {
        return "faq";
    }

    @GetMapping("/insert")
    public String insert(){
        CurrencyPair cp = new CurrencyPair();
        cp.setName("BTCUSDT");
        repo.save(cp);
        return "index";
    }
}
