package com.example.StudentFinanceTracker.Repository;

import com.example.StudentFinanceTracker.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICourseRepository extends JpaRepository<Course, Long> {
}
