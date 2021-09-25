package com.k4rnaj1k.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name="events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="event_id")
    private Long eventId;

    @JsonProperty("moduleName")
    @Enumerated(EnumType.STRING)
    private ModuleName moduleName;

    private String name;

    @ManyToOne
    private Group group;

    public static enum ModuleName{
        attendance,
        assign
    }

    public Event() {
    }

    public Event(Long eventId, ModuleName moduleName, String name, Group group) {
        this.eventId = eventId;
        this.moduleName = moduleName;
        this.name = name;
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public ModuleName getModuleName() {
        return moduleName;
    }

    public void setModuleName(ModuleName moduleName) {
        this.moduleName = moduleName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
