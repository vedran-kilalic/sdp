package com.example.StudentFinanceTracker.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.StudentFinanceTracker.Model.User;
import com.example.StudentFinanceTracker.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/students")
    public String showAllStudents(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "pages/students";
    }
    @GetMapping("/login")
    public String showLoginPage() {
        return "pages/login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        User user = userService.getByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user.getIsAdmin() == 1 ? "indexAdmin" : "index";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "pages/login";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "pages/login";
    }

}
