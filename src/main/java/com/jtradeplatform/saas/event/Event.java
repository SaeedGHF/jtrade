package com.jtradeplatform.saas.event;

import com.jtradeplatform.saas.currencyPair.CurrencyPair;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String pattern;
    @ManyToOne
    private CurrencyPair currencyPair;
    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }
}
