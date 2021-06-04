package com.jtradeplatform.saas.candlestick;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CandlestickService {
    CandlestickRepository repo;

    CandlestickService(CandlestickRepository candlestickRepository) {
        repo = candlestickRepository;
    }

    public void deleteAll(){
        repo.deleteAll();
    }

    public void test() {
        Candlestick c1 = new Candlestick();
        c1.open = 1.4456D;
        c1.high = 1.4856D;
        c1.low = 1.4256D;
        c1.close = 1.4355D;
        c1.period = 1;
        c1.currencyPair = 2;
        c1.time = Instant.now().minusSeconds(30);

        Candlestick c2 = new Candlestick();
        c2.open = 1.1456D;
        c2.high = 1.1856D;
        c2.low = 1.1256D;
        c2.close = 1.1355D;
        c2.period = 1;
        c2.currencyPair = 2;
        c2.time = Instant.now().minusSeconds(180);

        Candlestick c3 = new Candlestick();
        c3.open = 1.3456D;
        c3.high = 1.3856D;
        c3.low = 1.3256D;
        c3.close = 1.3355D;
        c3.period = 1;
        c3.currencyPair = 2;
        c3.time = Instant.now().minusSeconds(260);

        Candlestick c4 = new Candlestick();
        c4.open = 1.7456D;
        c4.high = 1.7856D;
        c4.low = 1.7256D;
        c4.close = 1.7355D;
        c4.period = 1;
        c4.currencyPair = 2;
        c4.time = Instant.now().minusSeconds(360);


        List<Candlestick> list = new ArrayList<>();
        list.add(c1);
        list.add(c2);
        list.add(c3);
        list.add(c4);

        repo.saveAll(list);
    }
}
