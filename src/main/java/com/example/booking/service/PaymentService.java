package com.example.booking.service;


import com.example.booking.entity.Booking;
import com.example.booking.entity.Payment;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository, BookingService bookingService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
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

}
