package com.k4rnaj1k.model;

import org.telegram.telegrambots.meta.api.objects.Chat;

import javax.persistence.*;

@Entity
@Table(name="user_chats")
public class UserChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinTable(name="user_chats_users", joinColumns = @JoinColumn(name="user_chat_id"), inverseJoinColumns = @JoinColumn(name="user_id"))
    private User user;

    private Long chatId;

    @Enumerated(EnumType.STRING)
    private State state;

    public UserChat() {
    }

    public UserChat(User user, Long chatId) {
        this.user = user;
        this.chatId = chatId;
        this.state = State.CONNECTED;
    }

    public UserChat(User user, Long chatId, State state) {
        this.user = user;
        this.chatId = chatId;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
