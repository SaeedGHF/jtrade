package com.jtradeplatform.saas.event;

import com.jtradeplatform.saas.candlestick.CandlestickRepository;
import com.jtradeplatform.saas.symbol.SymbolRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class EventService {

    CandlestickRepository candlestickRepository;
    SymbolRepository symbolRepository;
    EventRepository eventRepository;
    SimpMessagingTemplate simpMessagingTemplate;
    EntityManager entityManager;

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

    @Transactional
    public void removeOldEvents() {
        entityManager.createQuery("delete from Event where createdAt < :time")
                .setParameter("time", Date.from(Instant.now().minus(12, ChronoUnit.HOURS)), TemporalType.DATE)
                .executeUpdate();
    }
}
