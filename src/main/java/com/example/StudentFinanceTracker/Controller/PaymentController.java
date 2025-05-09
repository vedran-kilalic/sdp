package com.example.StudentFinanceTracker.Controller;

import com.example.StudentFinanceTracker.Model.Payment;
import com.example.StudentFinanceTracker.Service.PaymentService;
import com.example.StudentFinanceTracker.Model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/payments")
    public String getPaymentsForLoggedInUser(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Payment> payments = paymentService.getPaymentsForUser(loggedInUser.getId());
        model.addAttribute("payments", payments);
        return "pages/payment";
    }
    @GetMapping("/pay")
    public String showPaymentPage(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        return "pages/pay";
    }

}
