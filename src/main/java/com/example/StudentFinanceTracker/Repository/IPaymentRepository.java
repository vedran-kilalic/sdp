package com.example.StudentFinanceTracker.Repository;

import com.example.StudentFinanceTracker.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(Long userId);
    List<Payment> findByStatusAndUserId(String status, Long userId);
}
