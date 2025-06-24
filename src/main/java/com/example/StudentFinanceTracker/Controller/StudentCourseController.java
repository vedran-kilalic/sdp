package com.example.StudentFinanceTracker.Controller;

import com.example.StudentFinanceTracker.Model.StudentCourse;
import com.example.StudentFinanceTracker.Model.User;
import com.example.StudentFinanceTracker.Service.CourseService;
import com.example.StudentFinanceTracker.Service.StudentCourseService;
import com.example.StudentFinanceTracker.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/student-courses")
public class StudentCourseController {

    @Autowired private StudentCourseService studentCourseService;
    @Autowired private UserService userService;
    @Autowired private CourseService courseService;

    @GetMapping("/assign")
    public String showAssignForm(Model model) {
        model.addAttribute("studentCourse", new StudentCourse());
        model.addAttribute("students", userService.getAllUsers());
        model.addAttribute("courses", courseService.getAllCourses());
        return "pages/assignStudentCourse";
    }

    @PostMapping("/assign")
    public String assignCourse(@ModelAttribute StudentCourse sc, RedirectAttributes redirectAttributes) {
        sc.setEnrollmentDate(LocalDate.now());
        studentCourseService.save(sc);
        redirectAttributes.addFlashAttribute("toastMessage", "Course assigned to student successfully!");
        return "redirect:/students";
    }

    @GetMapping
    public String viewAllAssignments(Model model) {
        List<StudentCourse> all = studentCourseService.getAll();
        model.addAttribute("assignments", all);
        return "pages/studentCourseList";
    }

    @GetMapping("/my-courses")
    public String viewMyCourses(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getIsAdmin() != 0) {
            return "redirect:/login";
        }

        List<StudentCourse> myCourses = studentCourseService.getCoursesForStudent(user.getId());
        model.addAttribute("assignments", myCourses);
        return "pages/studentCourses";
    }
}
