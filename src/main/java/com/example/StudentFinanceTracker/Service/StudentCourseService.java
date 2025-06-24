package com.example.StudentFinanceTracker.Service;

import com.example.StudentFinanceTracker.Model.StudentCourse;
import com.example.StudentFinanceTracker.Repository.IStudentCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentCourseService {

    private final IStudentCourseRepository studentCourseRepository;

    @Autowired
    public StudentCourseService(IStudentCourseRepository repo) {
        this.studentCourseRepository = repo;
    }

    public List<StudentCourse> getAll() {
        return studentCourseRepository.findAll();
    }

    public StudentCourse save(StudentCourse sc) {
        return studentCourseRepository.save(sc);
    }

    public void delete(Long id) {
        studentCourseRepository.deleteById(id);
    }

    public List<StudentCourse> getCoursesForStudent(Long userId) {
        return studentCourseRepository.findByUser_Id(userId);
    }
}
