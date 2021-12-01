package com.jtradeplatform.saas.event;

import com.jtradeplatform.saas.symbol.Symbol;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;
    @Column
    private String type;
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
