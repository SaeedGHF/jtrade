package com.jtradeplatform.saas.currencyPair;

import javax.persistence.*;

@Entity
@Table(name="currency_pairs")
public class CurrencyPair {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique=true, nullable=false)
    private Long id;
    @Column(unique=true, nullable=false)
    private String name;

    public CurrencyPair() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
