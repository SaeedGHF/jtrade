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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    ApiController(
            SymbolRepository symbolRep,
            CandlestickRepository candlestickRep,
            EventRepository eventRep,
            CandlestickService candlestickService,
            SimpMessagingTemplate simpMessagingTemplate,
            BinanceSpotService binanceSpotService,
            SymbolService symbolService
    ) {
        this.symbolRep = symbolRep;
        this.candlestickRep = candlestickRep;
        this.eventRep = eventRep;
        this.candlestickService = candlestickService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.binanceSpotService = binanceSpotService;
        this.symbolService = symbolService;
    }

    @GetMapping("symbol/{symbol}")
    public Optional<Symbol> symbol(@PathVariable("symbol") int symbol) {
        return symbolRep.findById(symbol);
    }

    @GetMapping("chart/{symbol}")
    public List<Candlestick> chart(@PathVariable("symbol") int symbol) {
        return candlestickRep.findAllBySymbol(symbol);
    }

    @GetMapping("events/{symbol}")
    public List<Event> events(@PathVariable("symbol") int symbol) {
        return eventRep.findBySymbol(symbolRep.getById(symbol));
    }

    @GetMapping("events")
    public List<Event> eventsAll() {
        return eventRep.findAll();
    }

    @GetMapping("events/test/{symbol}")
    public void eventsTest(@PathVariable("symbol") int symbol) {
        Event event = new Event();
        event.setPattern("Пробой уровня");
        Optional<Symbol> s = symbolRep.findById(symbol);
        s.ifPresent(event::setSymbol);
        simpMessagingTemplate.convertAndSend("/events", event);
    }

    @PostMapping("chart/{symbol}/refresh")
    public void refreshChart(@PathVariable int symbol) {
        Optional<Symbol> symbolEntity = symbolRep.findById(symbol);
        symbolEntity.ifPresent(value -> candlestickService.updateSymbolChart(value));
    }

    @GetMapping("/symbol/refresh")
    public String symbol() {
        symbolService.refreshAll();
        return "faq";
    }

    @PostMapping("/charts/clear")
    public String clear() {
        candlestickService.deleteAll();
        return "faq";
    }
}
