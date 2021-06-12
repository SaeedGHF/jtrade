package com.jtradeplatform.saas.event;

import com.jtradeplatform.saas.candlestick.Candlestick;
import com.jtradeplatform.saas.candlestick.CandlestickRepository;
import com.jtradeplatform.saas.chart.PatternFinderContext;
import com.jtradeplatform.saas.chart.PatternResultContainer;
import com.jtradeplatform.saas.chart.patternsImpl.*;
import com.jtradeplatform.saas.symbol.Symbol;
import com.jtradeplatform.saas.symbol.SymbolRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TemporalType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class EventService {

    CandlestickRepository candlestickRepository;
    SymbolRepository symbolRepository;
    PatternFinderContext patternFinderContext;
    EventRepository eventRepository;
    SimpMessagingTemplate simpMessagingTemplate;
    EntityManager entityManager;

    public void findPatternsAndSend() {

        List<Symbol> symbolList = symbolRepository.findAll();
        patternFinderContext.setPatternClasses(Arrays.asList(
                //CascadePattern.class,
                //Speed30Pattern.class,
                MaxMinPattern.class
        ));

        for (Symbol symbol : symbolList) {
            List<Candlestick> candlestickList = candlestickRepository.findAllBySymbol(symbol.getId());
            PatternResultContainer resultContainer = patternFinderContext.find(candlestickList);
            if (resultContainer.isEmpty()) {
                continue;
            }

            Event event = new Event(symbol, resultContainer.toString());
            this.saveAndSend(event, "/events");
        }
    }

    public void saveAndSend(Event event, String destination) {
        List<Event> oldEvent = entityManager
                .createQuery("select object(e) from Event e where e.symbol = :s and e.createdAt >= :d" +
                        " order by e.id desc ", Event.class)
                .setParameter("s", event.getSymbol())
                .setParameter("d", Date.from(Instant.now().minus(30, ChronoUnit.MINUTES)), TemporalType.DATE)
                .getResultList();


        if (!oldEvent.isEmpty()) {
            return;
        }

        eventRepository.save(event);
        simpMessagingTemplate.convertAndSend(destination, event);
    }
}
