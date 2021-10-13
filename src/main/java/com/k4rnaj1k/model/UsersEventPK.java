package com.k4rnaj1k.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsersEventPK implements Serializable {
    @Column(name = "user_id")
    private long user;
    @Column(name = "event_id")
    private long event;

    public UsersEventPK() {
    }

    public UsersEventPK(Long id, Long eventId) {
        this.user = id;
        this.event = eventId;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public long getEvent() {
        return event;
    }

    public void setEvent(long event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersEventPK that = (UsersEventPK) o;
        return Objects.equals(event, that.event) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, user);
    }
}
