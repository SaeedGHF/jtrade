package com.jtradeplatform.saas;

import com.jtradeplatform.saas.candlestick.Candlestick;
import com.jtradeplatform.saas.candlestick.CandlestickRepository;
import com.jtradeplatform.saas.event.Event;
import com.jtradeplatform.saas.event.EventRepository;
import com.jtradeplatform.saas.symbol.Symbol;
import com.jtradeplatform.saas.symbol.SymbolRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api")
public class ApiController {

    SymbolRepository symbolRep;
    CandlestickRepository candlestickRep;
    EventRepository eventRep;

    ApiController(SymbolRepository symbolRep, CandlestickRepository candlestickRep, EventRepository eventRep) {
        this.symbolRep = symbolRep;
        this.candlestickRep = candlestickRep;
        this.eventRep = eventRep;
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

    @PostMapping("chart/{symbol}/refresh")
    public void updateChart(@PathVariable int symbol) {
        // TODO: refresh currency history
    }
}
