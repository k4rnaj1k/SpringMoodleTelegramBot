package com.k4rnaj1k.model;

import com.k4rnaj1k.dto.CourseDTO;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Table(name = "courses")
@Entity
public class Course {
    @Id
    private Long courseId;

    @Column(name = "course_name")
    private String fullName;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToMany
    @JoinTable(name = "users_courses", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users = new ArrayList<>();

    public Course() {
    }

    public Course(Long courseId, String fullName, String shortName) {
        this.courseId = courseId;
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public static Course fromDTO(CourseDTO courseDTO) {
        return new Course(courseDTO.id(), courseDTO.fullName(), courseDTO.shortName());
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant timeUpdated) {
        this.updatedAt = timeUpdated;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        this.users.add(user);
        user.addCourse(this);
    }
}