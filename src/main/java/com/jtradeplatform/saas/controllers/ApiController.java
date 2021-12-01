package com.jtradeplatform.saas.controllers;

import com.jtradeplatform.saas.candlestick.Candlestick;
import com.jtradeplatform.saas.candlestick.CandlestickRepository;
import com.jtradeplatform.saas.candlestick.CandlestickService;
import com.jtradeplatform.saas.event.Event;
import com.jtradeplatform.saas.event.EventRepository;
import com.jtradeplatform.saas.services.BinanceSpotService;
import com.jtradeplatform.saas.symbol.Symbol;
import com.jtradeplatform.saas.symbol.SymbolRepository;
import com.jtradeplatform.saas.symbol.SymbolService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping(path = "api")
public class ApiController {

    CandlestickService candlestickService;
    SimpMessagingTemplate simpMessagingTemplate;
    BinanceSpotService binanceSpotService;
    SymbolService symbolService;
    SymbolRepository symbolRep;
    CandlestickRepository candlestickRep;
    EventRepository eventRep;

    @GetMapping("symbols/{symbol}")
    public Optional<Symbol> symbol(@PathVariable("symbol") int symbol) {
        return symbolRep.findById(symbol);
    }

    @GetMapping("chart/{symbol}")
    public List<Candlestick> chart(@PathVariable("symbol") int symbolId) {
        Optional<Symbol> symbol = symbolRep.findById(symbolId);
        if (symbol.isPresent()) {
            return candlestickRep.findAllBySymbol(symbol.get());
        }
        return new ArrayList<>();
    }

    @GetMapping("events/{symbol}")
    public List<Event> events(@PathVariable("symbol") int symbol) {
        return eventRep.findBySymbol(symbolRep.getById(symbol));
    }

    @GetMapping("events")
    public List<Event> eventsAll() {
        return eventRep.findAll();
    }

    @PostMapping("chart/{symbol}/refresh")
    public void refreshSymbolChart(@PathVariable int symbol) {
        Optional<Symbol> symbolEntity = symbolRep.findById(symbol);
        symbolEntity.ifPresent(value -> candlestickService.updateSymbolChart(value));
    }

    @PostMapping("chart/refresh")
    public void refreshAllCharts() {
        candlestickService.updateAllCharts();
    }

    @PostMapping("/symbols/refresh")
    public String symbol() {
        symbolService.refreshAll();
        return "ok";
    }

    @PostMapping("/charts/clear")
    public String clear() {
        candlestickService.deleteAll();
        return "ok";
    }
}
