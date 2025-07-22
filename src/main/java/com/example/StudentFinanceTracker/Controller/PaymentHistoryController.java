package com.example.StudentFinanceTracker.Controller;

import com.example.StudentFinanceTracker.Model.Payment;
import com.example.StudentFinanceTracker.Model.PaymentHistory;
import com.example.StudentFinanceTracker.Model.PaymentStatus;
import com.example.StudentFinanceTracker.Model.User;
import com.example.StudentFinanceTracker.Service.PaymentHistoryService;
import com.example.StudentFinanceTracker.Service.PaymentService;
import com.example.StudentFinanceTracker.Service.PdfGeneratorService;
import com.example.StudentFinanceTracker.Service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class PaymentHistoryController {

    @Autowired
    private PaymentHistoryService paymentHistoryService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;
    @Autowired
    private UserService userService;


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
    @GetMapping("/payments/report/{id}")
    public void downloadPaymentReport(@PathVariable("id") Long paymentId, HttpServletResponse response) throws IOException {
        Payment payment = paymentService.getPaymentById(paymentId);
        User user = userService.getUserById(payment.getUserId());

        byte[] pdfData = pdfGeneratorService.generatePaymentReport(user, payment);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=payment_report.pdf");
        response.getOutputStream().write(pdfData);
        response.getOutputStream().flush();
    }


}
