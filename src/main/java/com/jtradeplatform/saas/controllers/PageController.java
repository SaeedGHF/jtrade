package com.jtradeplatform.saas.controllers;

import com.jtradeplatform.saas.symbol.SymbolRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
@AllArgsConstructor
public class PageController {

    SymbolRepository symbolRepository;

    @GetMapping
    public String main() {
        return "index";
    }

    @GetMapping("/symbols")
    public String symbols(@ModelAttribute("model") ModelMap model) {
        model.addAttribute("symbols", symbolRepository.findAll());
        return "symbols";
    }
}
