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
    @GetMapping("/students/create")
    public String showCreateStudentForm(Model model) {
        model.addAttribute("user", new User());
        return "pages/createStudent";
    }
    @PostMapping("/students/create")
    public String createStudent(@ModelAttribute User user) {
        user.setIsAdmin(0);
        user.setPosition("Student");
        userService.saveUser(user);
        return "redirect:/students";

    }
    @GetMapping("/students/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user != null && user.getIsAdmin() == 0) {
            model.addAttribute("user", user);
            return "pages/editStudent";
        }
        return "pages/students";
    }

    @PostMapping("/students/update/{id}")
    public String updateStudent(@PathVariable Long id,
                                @ModelAttribute User updatedUser,
                                @RequestParam(required = false) String newPassword) {
        User existing = userService.getUserById(id);
        if (existing != null && existing.getIsAdmin() == 0) {
            updatedUser.setId(id);
            updatedUser.setIsAdmin(0);
            updatedUser.setPosition(existing.getPosition());
            updatedUser.setPassword(
                    (newPassword != null && !newPassword.isBlank())
                            ? newPassword
                            : existing.getPassword()
            );
            userService.saveUser(updatedUser);
        }
        return "redirect:/students";
    }


    @GetMapping("/students/delete/{id}")
    public String showDeleteForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null || user.getIsAdmin() != 0) {
            return "pages/students";
        }
        model.addAttribute("user", user);
        return "pages/deleteStudent";
    }

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null && user.getIsAdmin() == 0) {
            userService.deleteUser(id);
        }
        return "redirect:/students";
    }


    @GetMapping("/login")
    public String showLoginPage() {
        return "pages/login";
    }


    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {
        User user = userService.getByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", user);
            return user.getIsAdmin() == 1 ? "redirect:/admin" : "redirect:/student";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "pages/login";
        }
    }
    @GetMapping("/reset-password")
    public String showResetPasswordPage() {
        return "pages/resetPassword";
    }

//    @PostMapping("/reset-password")
//    public String resetPassword() {
//        return "pages/resetPassword";
//    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "pages/login";
    }

    @GetMapping("/profile")
    public String studentProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getIsAdmin() == 1) {
            return "pages/login";
        }
        model.addAttribute("user", user);
        return "pages/profile";
    }

    @GetMapping("/adminProfile")
    public String adminProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getIsAdmin() != 1) {
            return "pages/login";
        }
        model.addAttribute("user", user);
        return "pages/adminProfile";
    }


}
