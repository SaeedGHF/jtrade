package com.jtradeplatform.saas.symbol;

import com.binance.client.model.market.ExchangeInfoEntry;
import com.jtradeplatform.saas.services.BinanceFutures;
import org.springframework.stereotype.Service;

@Service
public class SymbolService {
    BinanceFutures binanceFutures;
    SymbolRepository symbolRepository;

    SymbolService(BinanceFutures binanceFutures, SymbolRepository symbolRepository) {
        this.binanceFutures = binanceFutures;
        this.symbolRepository = symbolRepository;
    }

    public void refreshAll() {
        for (ExchangeInfoEntry entry : binanceFutures.getExchangeInfo().getSymbols()) {
            String symbolName = entry.getSymbol();
            Symbol symbol = symbolRepository.findByName(symbolName);
            if (symbol == null) {
                symbol = new Symbol();
                symbol.setName(symbolName);
            }
            symbol.setPricePrecision(entry.getPricePrecision());

            symbolRepository.save(symbol);
        }
    }
}
