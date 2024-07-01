package com.example.booking.service;


import com.example.booking.entity.Booking;
import com.example.booking.entity.Payment;
import com.example.booking.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void createPayment(Payment payment){
        paymentRepository.save(payment);

    }
    public List<Payment> showPaymentListOfGuest(Long userId){
        return paymentRepository.findAllByBooking_User_IdOrderByPaymentTimeDesc(userId);
    }
    public Page<Payment> showPaymentListOfAdmin(Pageable pageable){
        return paymentRepository.findAllByOrderByPaymentTimeDesc(pageable);
    }
    public Payment checkPayment(int paymentId){
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("payment not found"));
    }
    public double getTotalPayments() {
        return paymentRepository.findAll().stream().mapToDouble(Payment::getTotalPayment).sum();
    }


}
