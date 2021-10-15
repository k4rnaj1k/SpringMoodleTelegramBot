package com.k4rnaj1k.repository;

import com.k4rnaj1k.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
