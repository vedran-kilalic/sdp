package com.example.StudentFinanceTracker.Controller;

import com.example.StudentFinanceTracker.Model.Course;
import com.example.StudentFinanceTracker.Model.StudentCourse;
import com.example.StudentFinanceTracker.Model.User;
import com.example.StudentFinanceTracker.Service.CourseService;
import com.example.StudentFinanceTracker.Service.StudentCourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CourseController {

    private final CourseService courseService;
    private final StudentCourseService studentCourseService;

    @Autowired
    public CourseController(CourseService courseService, StudentCourseService studentCourseService) {
        this.courseService = courseService;
        this.studentCourseService = studentCourseService;
    }

    @GetMapping("/courses")
    public String showAllCourses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "pages/courses";
    }

    @GetMapping("/studentCourses")
    public String getCoursesForStudent(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getIsAdmin() != 0) {
            return "redirect:/login";
        }

        List<StudentCourse> assignments = studentCourseService.getCoursesForStudent(user.getId());
        List<Course> courses = assignments.stream()
                .map(StudentCourse::getCourse)
                .collect(Collectors.toList());

        model.addAttribute("courses", courses);
        return "pages/studentCourses";
    }
}
