package com.k4rnaj1k.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "chat_id")
    private Long chatId;

    @Enumerated(EnumType.STRING)
    private State state;

    @OneToMany(mappedBy = "user")
    private List<UsersEvent> usersEvents = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_groups", joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<Group> groups = new ArrayList<>();

    private boolean receiveNotifications;

    public User(Long chatId) {
        this.chatId = chatId;
        this.state = State.START;
        this.receiveNotifications = false;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public boolean isReceiveNotifications() {
        return receiveNotifications;
    }

    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    public void addGroup(Group group) {
        this.groups.add(group);
    }

    public void addUsersEvent(UsersEvent usersEvent) {
        this.usersEvents.add(usersEvent);
    }

    public List<UsersEvent> getUsersEvents() {
        return usersEvents;
    }

    public void setUsersEvents(List<UsersEvent> usersEvents) {
        this.usersEvents = usersEvents;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
