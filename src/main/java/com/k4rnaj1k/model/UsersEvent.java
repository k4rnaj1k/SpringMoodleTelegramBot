package com.k4rnaj1k.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users_events")
public class UsersEvent implements Serializable {

    @EmbeddedId
    private UsersEventPK id;

    @MapsId("user")
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @MapsId("event")
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    private boolean notified;

    public UsersEvent(User user, Event event) {
        setUser(user);
        setEvent(event);
        this.id = new UsersEventPK(user.getId(), event.getEventId());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        user.addUsersEvent(this);
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
        event.addUsersEvent(this);
    }

    public boolean isNotified() {
        return notified;
    }

    public UsersEventPK getId() {
        return id;
    }

    public void setId(UsersEventPK id) {
        this.id = id;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public UsersEvent() {

    }
}
