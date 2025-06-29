package com.example.StudentFinanceTracker.Controller;

import com.example.StudentFinanceTracker.Model.Payment;
import com.example.StudentFinanceTracker.Model.User;
import com.example.StudentFinanceTracker.Service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Value("${stripe.api.secret-key}")
    private String stripeSecretKey;

    @GetMapping("/payments")
    public String getPaymentsForLoggedInUser(HttpSession session,
                                             @RequestParam(required = false) String status,
                                             Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        // Fetch only "To Be Paid" payments for this user
        List<Payment> payments = paymentService.getPaymentsByStatusAndUserId("To Be Paid", loggedInUser.getId());
        model.addAttribute("payments", payments);

        if ("success".equals(status)) {
            model.addAttribute("paymentMessage", "Payment completed successfully.");
        } else if ("cancel".equals(status)) {
            model.addAttribute("paymentMessage", "Payment was canceled.");
        }

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

    @GetMapping("/create-checkout-session")
    @ResponseBody
    public Map<String, String> createCheckoutSession(
            @RequestParam long amount,
            @RequestParam String email,
            @RequestParam Long paymentId
    ) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/payment-success?paymentId=" + paymentId)
                .setCancelUrl("http://localhost:8080/payments?status=cancel&paymentId=" + paymentId)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount(amount)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Student Payment")
                                        .build())
                                .build())
                        .build())
                .setCustomerEmail(email)
                .build();

        Session session = Session.create(params);
        return Map.of("sessionUrl", session.getUrl());
    }

}
