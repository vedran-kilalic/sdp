package com.example.StudentFinanceTracker.Controller;

import com.example.StudentFinanceTracker.Model.PaymentHistory;
import com.example.StudentFinanceTracker.Model.User;
import com.example.StudentFinanceTracker.Service.PaymentHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PaymentHistoryController {

    @Autowired
    private PaymentHistoryService paymentHistoryService;

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
}
