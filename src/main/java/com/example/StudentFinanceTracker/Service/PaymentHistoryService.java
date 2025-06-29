package com.example.StudentFinanceTracker.Service;

import com.example.StudentFinanceTracker.Model.PaymentHistory;
import com.example.StudentFinanceTracker.Repository.IPaymentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentHistoryService {

    @Autowired
    private IPaymentHistoryRepository paymentHistoryRepository;

    public List<PaymentHistory> getHistoryForUser(Long userId) {
        return paymentHistoryRepository.findByUserId(userId);
    }
    public void save(PaymentHistory history) {
        paymentHistoryRepository.save(history);
    }

}
