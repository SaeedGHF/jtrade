package com.jtradeplatform.saas.symbol;

import com.jtradeplatform.saas.event.Event;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "symbols")
public class Symbol {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false)
    private Long pricePrecision;
    @OneToMany
    @JoinColumn(name = "symbol_id")
    private List<Event> events;
    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Symbol() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getPricePrecision() {
        return pricePrecision;
    }

    public void setPricePrecision(Long pricePrecision) {
        this.pricePrecision = pricePrecision;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}