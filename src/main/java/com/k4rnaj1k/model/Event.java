package com.k4rnaj1k.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private List<UsersEvent> usersEvents = new ArrayList<>();


    @Column(name = "event_id")
    private Long eventId;

    @JsonProperty("moduleName")
    @Enumerated(EnumType.STRING)
    private ModuleName moduleName;

    private String name;

    @ManyToOne
    @JoinTable(name = "event_courses", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Course course;

    @ManyToMany
    @JoinTable(name = "event_groups", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Group> groups = new ArrayList<>();

    private Instant timeStart;

    public Event(Long eventId, ModuleName moduleName, String name, Instant timeStart) {
        this.eventId = eventId;
        this.moduleName = moduleName;
        this.name = name;
        this.timeStart = timeStart;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void addUsersEvent(UsersEvent usersEvent) {
        this.usersEvents.add(usersEvent);
    }

    public void addGroup(Group eventsGroup) {
        this.groups.add(eventsGroup);
    }

    public enum ModuleName {
        attendance,
        assign
    }

    public List<User> getUsers(){
        if(groups.isEmpty())
            return course.getUsers();
        else
            return groups.get(0).getUsers();
    }

    public Instant getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Instant timeStart) {
        this.timeStart = timeStart;
    }

    public Event() {
    }

    public Event(Long eventId, ModuleName moduleName, String name, List<Group> groups, Instant timeStart) {
        this.eventId = eventId;
        this.moduleName = moduleName;
        this.name = name;
        this.groups = groups;
        this.timeStart = timeStart;
    }

    public Event(Long eventId, ModuleName moduleName, String name, List<Group> groups, Instant timeStart, Course course) {
        this.eventId = eventId;
        this.moduleName = moduleName;
        this.name = name;
        this.groups = groups;
        this.timeStart = timeStart;
        this.course = course;
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

    public List<UsersEvent> getUsersEvents() {
        return usersEvents;
    }

    public void setUsersEvents(List<UsersEvent> usersEvents) {
        this.usersEvents = usersEvents;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
