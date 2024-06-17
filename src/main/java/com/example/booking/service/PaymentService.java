package com.example.booking.service;


import com.example.booking.entity.Booking;
import com.example.booking.entity.Payment;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Async
    public Payment createPayment(int bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking with ID " + bookingId + " not found."));
        Payment newPayment = new Payment();
        newPayment.setPaymentTime(new Date());
        newPayment.setTotalPayment(booking.getTotalPrice());
        bookingService.updatePaymentStatus(booking.getBookingId());
        paymentRepository.save(newPayment);
        return newPayment;

    }
    @Async
    public Payment updatePayment(Payment payment){
        Payment existingPayment = paymentRepository.findById(payment.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("Not found"));
        existingPayment.setTotalPayment(payment.getTotalPayment());
        paymentRepository.save(existingPayment);
        return existingPayment;
    }
    public List<Payment> showPaymentListOfGuest(Long userId){
        return paymentRepository.findAllByBooking_User_Id(userId);
    }
    public List<Payment> showPaymentListOfAdmin(){
        return paymentRepository.findAll();
    }
    public Payment checkPayment(int paymentId){
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("payment not found"));
    }

}
