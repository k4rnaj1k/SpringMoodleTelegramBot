package com.k4rnaj1k.model;

import javax.persistence.*;

@Entity
@Table(name="events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type eventType;

    private enum Type{

    }
}
