package com.example.StudentFinanceTracker.Service;

import com.example.StudentFinanceTracker.Model.Payment;
import com.example.StudentFinanceTracker.Model.PaymentHistory;
import com.example.StudentFinanceTracker.Model.PaymentStatus;
import com.example.StudentFinanceTracker.Model.User;
import com.example.StudentFinanceTracker.Repository.IPaymentRepository;
import com.example.StudentFinanceTracker.Security.MailService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    private IPaymentRepository paymentRepository;
    @Autowired
    private PaymentHistoryService paymentHistoryService;
    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    public List<Payment> getPaymentsForUser(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }
    public List<Payment> getPaymentsByStatusAndUserId(String status, Long userId) {
        return paymentRepository.findByStatusAndUserId(status, userId);
    }

    @Transactional
    public void completePayment(Long paymentId, Long userId) {
        Payment payment = getPaymentById(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found");
        }

        LocalDate today = LocalDate.now();
        LocalDate dueDate = payment.getPaymentDate();

        payment.setStatus("Paid");
        paymentRepository.save(payment);

        PaymentHistory history = new PaymentHistory();
        history.setUserId(userId);
        history.setPaymentId(paymentId);
        history.setStatus(today.isBefore(dueDate) || today.isEqual(dueDate)
                ? PaymentStatus.PAID_ON_TIME
                : PaymentStatus.PAID_LATE);
        history.setPayDate(today);
        paymentHistoryService.save(history);

        try {
            User user = userService.getUserById(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("payerName", user.getFullName());
            data.put("amount", payment.getAmount());
            data.put("paymentDate", today);
            data.put("accountNumber", "1610000000000012");
            data.put("installmentMonth", payment.getDescription());

            byte[] pdf = pdfGeneratorService.generatePdfFromTemplate("pages/paymentReport", data);
            mailService.sendPaymentReport(user.getEmail(), pdf);

        } catch (Exception e) {
            System.err.println("Error in sending report in email form: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
