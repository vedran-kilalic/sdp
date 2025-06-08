package com.example.StudentFinanceTracker.Repository;

import com.example.StudentFinanceTracker.Model.Course;
import com.example.StudentFinanceTracker.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUser_Id(Long userId);

}
