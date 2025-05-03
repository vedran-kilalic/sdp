package com.example.StudentFinanceTracker.Controller;

import org.springframework.ui.Model;
import com.example.StudentFinanceTracker.Model.User;
import com.example.StudentFinanceTracker.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
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
        return "students";
    }
}
