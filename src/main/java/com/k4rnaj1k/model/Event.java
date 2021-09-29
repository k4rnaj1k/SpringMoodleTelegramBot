package com.k4rnaj1k.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

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

    @ManyToMany
    @JoinTable(name = "event_groups", joinColumns = @JoinColumn(name="group_id"), inverseJoinColumns = @JoinColumn(name="event_id"))
    private List<Group> groups;

    private Instant timeStart;

    public enum ModuleName{
        attendance,
        assign
    }


    public Instant getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Instant timeStart) {
        this.timeStart = timeStart;
    }

    public Event() {
    }

    public Event(Long eventId, ModuleName moduleName, String name, List<Group> groups) {
        this.eventId = eventId;
        this.moduleName = moduleName;
        this.name = name;
        this.groups = groups;
        this.timeStart = Instant.now();
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

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroup(List<Group> groups) {
        this.groups = groups;
    }
}
