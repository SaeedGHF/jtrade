package com.jtradeplatform.saas.event;

import com.jtradeplatform.saas.symbol.Symbol;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;
    private String pattern;
    @ManyToOne
    @JoinColumn(name = "symbol_id")
    private Symbol symbol;
    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }
}
