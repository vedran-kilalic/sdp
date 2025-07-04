package com.example.StudentFinanceTracker.Controller;

import com.example.StudentFinanceTracker.Model.Payment;
import com.example.StudentFinanceTracker.Model.PaymentHistory;
import com.example.StudentFinanceTracker.Model.PaymentStatus;
import com.example.StudentFinanceTracker.Model.User;
import com.example.StudentFinanceTracker.Service.PaymentHistoryService;
import com.example.StudentFinanceTracker.Service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class PaymentHistoryController {

    @Autowired
    private PaymentHistoryService paymentHistoryService;

    @Autowired
    private PaymentService paymentService;


    @GetMapping("/paymentHistory")
    public String getPaymentHistoryForUser(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<PaymentHistory> history = paymentHistoryService.getHistoryForUser(loggedInUser.getId());
        model.addAttribute("paymentHistory", history);
        return "pages/paymentHistory";
    }

    @GetMapping("/payment-success")
    public String handleSuccess(@RequestParam Long paymentId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        paymentService.completePayment(paymentId, user.getId());
        return "redirect:/paymentHistory";
    }
}
