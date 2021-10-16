package com.k4rnaj1k.repository;

import com.k4rnaj1k.model.Course;
import com.k4rnaj1k.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> getCoursesByUsersContaining(User user);
}
