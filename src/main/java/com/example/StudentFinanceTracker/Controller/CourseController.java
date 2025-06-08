package com.example.StudentFinanceTracker.Controller;

import com.example.StudentFinanceTracker.Model.Course;
import com.example.StudentFinanceTracker.Model.User;
import com.example.StudentFinanceTracker.Service.CourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService s) {
        this.courseService = s;
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

        List<Course> courses = courseService.getCoursesByUserId(user.getId());
        model.addAttribute("courses", courses);
        return "pages/studentCourses";
    }
}
