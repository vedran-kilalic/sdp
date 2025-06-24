package com.example.StudentFinanceTracker.Controller;

import com.example.StudentFinanceTracker.Security.JwtUtil;
import com.example.StudentFinanceTracker.Security.MailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.StudentFinanceTracker.Model.User;
import com.example.StudentFinanceTracker.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;
    private final UserService userService;
    private final MailService mailService;

    @Autowired
    public UserController(UserService userService , MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
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
    public String createStudent(@ModelAttribute User user,RedirectAttributes redirectAttributes) {
        user.setIsAdmin(0);
        user.setPosition("Student");
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("toastMessage", "Student created successfully!");
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
    public String updateStudent(@PathVariable Long id,RedirectAttributes redirectAttributes,
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
            redirectAttributes.addFlashAttribute("toastMessage", "Student updated successfully!");
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
    public String deleteStudent(@PathVariable Long id , RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(id);
        if (user != null && user.getIsAdmin() == 0) {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("toastMessage", "Student deleted successfully!");
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
        if (user != null && userService.checkPassword(password, user.getPassword())) {
            session.setAttribute("loggedInUser", user);
            String jwtToken = jwtUtil.generateToken(user.getEmail());
            session.setAttribute("jwt", jwtToken);

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

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        User user = userService.getByEmail(email);
        if (user != null) {
            mailService.sendResetPasswordEmail(email);
            redirectAttributes.addFlashAttribute("toastMessage", "Reset link sent to your email.");
        } else {
            redirectAttributes.addFlashAttribute("toastMessage", "Email not found.");
        }
        return "redirect:/login";
    }

    @GetMapping("/change-password")
    public String showChangeUserPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "pages/changePassword";
    }

    @PostMapping("/change-password")
    public String changeUserPassword(@RequestParam("token") String token,
                                     @RequestParam("currentPassword") String currentPassword,
                                     @RequestParam("newPassword") String newPassword,
                                     RedirectAttributes redirectAttributes) {

        boolean success = userService.changePassword(token, currentPassword, newPassword);

        if (success) {
            redirectAttributes.addFlashAttribute("toastMessage", "Password updated successfully.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid current password or token.");
            return "redirect:/change-password?token=" + token;
        }
    }

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
