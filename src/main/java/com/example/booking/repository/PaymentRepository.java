package com.example.booking.repository;

import com.example.booking.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    List<Payment> findAllByBooking_User_IdOrderByPaymentTimeDesc(Long userId);
    Page<Payment> findAllByOrderByPaymentTimeDesc(Pageable pageable);
    List<Payment> findAllByBooking_User_Id(Long userId);
    Payment findByPaymentIdAndPaymentPin(int paymentId, String pin);
}
