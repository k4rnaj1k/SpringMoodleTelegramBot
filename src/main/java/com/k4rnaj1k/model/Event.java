package com.k4rnaj1k.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    @JoinTable(name = "events_courses", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Course course;

    @ManyToOne
    @JoinTable(name = "events_groups", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Group group;

    private Instant timeStart;

    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name="event_type")
    private EventType eventType;

    public enum EventType{
        due,
        open,
        close,
        attendance
    }

    public enum ModuleName {
        attendance,
        assign,
        quiz
    }


    public Event(Long eventId, ModuleName moduleName, String name, Instant timeStart, String url, EventType eventType) {
        this.eventId = eventId;
        this.moduleName = moduleName;
        this.name = name;
        this.timeStart = timeStart;
        this.url = url;
        this.eventType = eventType;
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

    public void setGroup(Group eventsGroup) {
        this.group = eventsGroup;
    }

    public Group getGroup() {
        return group;
    }

    @Transactional
    public Set<User> getUsers() {
        if (group != null)
            return group.getUsers();
        else
            return course.getUsers();
    }

    public Instant getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Instant timeStart) {
        this.timeStart = timeStart;
    }

    public Event() {
    }

    public Event(Long eventId, ModuleName moduleName, String name, Group group, Instant timeStart) {
        this.eventId = eventId;
        this.moduleName = moduleName;
        this.name = name;
        this.group = group;
        this.timeStart = timeStart;
    }

    public Event(Long eventId, ModuleName moduleName, String name, Group group, Instant timeStart, Course course) {
        this.eventId = eventId;
        this.moduleName = moduleName;
        this.name = name;
        this.group = group;
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

    public List<UsersEvent> getUsersEvents() {
        return usersEvents;
    }

    public void setUsersEvents(List<UsersEvent> usersEvents) {
        this.usersEvents = usersEvents;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id.equals(event.id) && eventId.equals(event.eventId) && name.equals(event.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventId, name);
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
