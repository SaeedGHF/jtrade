package com.jtradeplatform.saas.event;

import com.jtradeplatform.saas.symbol.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findBySymbol(Symbol symbol);
}
