package com.example.StudentFinanceTracker.Repository;

import com.example.StudentFinanceTracker.Model.Course;
import com.example.StudentFinanceTracker.Model.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IStudentCourseRepository extends JpaRepository<StudentCourse, Long> {

    List<StudentCourse> findByUser_Id(Long userId);
}
