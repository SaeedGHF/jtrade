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
    @Column(columnDefinition = "TEXT")
    private String data;
    @ManyToOne
    @JoinColumn(name = "symbol_id")
    private Symbol symbol;
    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    public Event(){

    }

    public Event(Symbol symbol, String pattern){
        this.symbol = symbol;
        this.data = pattern;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", symbol=" + symbol +
                ", createdAt=" + createdAt +
                '}';
    }
}
