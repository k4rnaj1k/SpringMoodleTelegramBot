package com.k4rnaj1k.model;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name="groups")
public class Group {
    @Id
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "groups")
    private List<User> users;

    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
