package com.k4rnaj1k.model;

import com.k4rnaj1k.dto.upcoming.CourseDTO;
import com.k4rnaj1k.util.TelegramUtil;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

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
    private Set<User> users = new LinkedHashSet<>();

    public Course() {
    }

    public Course(Long courseId, String fullName, String shortName) {
        this.courseId = courseId;
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public static Course fromDTO(CourseDTO courseDTO) {
        return new Course(courseDTO.id(), courseDTO.fullName(), courseDTO.shortName().length() > 5 ? TelegramUtil.acronym(courseDTO.shortName()) : courseDTO.shortName());
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        this.users.add(user);
        user.addCourse(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseId, course.courseId) && Objects.equals(fullName, course.fullName) && Objects.equals(shortName, course.shortName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, fullName, shortName);
    }
}