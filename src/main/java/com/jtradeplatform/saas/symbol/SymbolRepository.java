package com.jtradeplatform.saas.symbol;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SymbolRepository extends JpaRepository<Symbol, Integer> {
    public Symbol findByName(String name);
}
