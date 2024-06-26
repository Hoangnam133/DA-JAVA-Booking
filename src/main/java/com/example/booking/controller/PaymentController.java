package com.example.booking.controller;

import com.example.booking.entity.Booking;
import com.example.booking.entity.Payment;
import com.example.booking.service.BookingService;
import com.example.booking.service.PaymentService;
import com.example.booking.service.PaymentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final PaymentTypeService paymentTypeService;

    @Autowired
    public PaymentController(PaymentService paymentService, BookingService bookingService, PaymentTypeService paymentTypeService){
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.paymentTypeService = paymentTypeService;
    }
    @GetMapping("/showAdminPaymentList")
    public String showAdminPaymentList(@RequestParam(defaultValue = "0") int page, Model model){
        try {
            Pageable pageable = PageRequest.of(page, 4);
            Page<Payment> paymentPage = paymentService.showPaymentListOfAdmin(pageable);
            model.addAttribute("payments", paymentPage.getContent());
            model.addAttribute("totalPages", paymentPage.getTotalPages());
            model.addAttribute("totalPages", paymentPage.getTotalPages());
            model.addAttribute("currentPage", page);
            return "ListOfAdmin/paymentList";
        } catch (Exception e) {
            model.addAttribute("Errors", e);
            return "errors";
        }
    }
    // Payment method is cash
    @GetMapping("/add/{bookingId}")
    public String showAddPaymentForm(@PathVariable("bookingId") int bookingId, Model model){
        try {
            Booking booking = bookingService.findBookingById(bookingId);
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setPaymentType(paymentTypeService.getPaymentTypeById(2));
            payment.setTotalPayment(booking.getTotalPrice());
            model.addAttribute("payment", payment);
            return "Payments/add";
        }catch (Exception e){
            model.addAttribute("Errors", e);
            return "errors";
        }
    }
    @PostMapping("/save/{bookingId}")
    public String savePayment(@PathVariable("bookingId") int bookingId, Payment payment, Model model){
        try {


            Booking booking = bookingService.findBookingById(bookingId);
            payment.setBooking(booking);
            paymentService.createPayment(payment);
            bookingService.updatePaymentStatus(booking.getBookingId());
            return "redirect:/payments/showAdminPaymentList";
        }catch (Exception e){
            model.addAttribute("Errors", e);
            return "errors";
        }
    }



}
