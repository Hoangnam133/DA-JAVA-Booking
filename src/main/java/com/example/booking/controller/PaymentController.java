package com.example.booking.controller;

import com.example.booking.config.PaypalPaymentIntent;
import com.example.booking.config.PaypalPaymentMethod;
import com.example.booking.entity.Booking;
import com.example.booking.entity.Payment;
import com.example.booking.service.BookingService;
import com.example.booking.service.PaymentService;
import com.example.booking.service.PaymentTypeService;
import com.example.booking.util.URLUtils;
import com.paypal.api.payments.Links;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.booking.service.PaypalService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final PaymentTypeService paymentTypeService;


    @Autowired
    public PaymentController(PaymentService paymentService, BookingService bookingService, PaymentTypeService paymentTypeService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.paymentTypeService = paymentTypeService;
    }

    @GetMapping("/showAdminPaymentList")
    public String showAdminPaymentList(@RequestParam(defaultValue = "0") int page, Model model) {
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
    public String showAddPaymentForm(@PathVariable("bookingId") int bookingId, Model model) {
        try {
            Booking booking = bookingService.findBookingById(bookingId);
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setPaymentType(paymentTypeService.getPaymentTypeById(2));
            payment.setTotalPayment(booking.getTotalPrice());
            model.addAttribute("payment", payment);
            return "Payments/add";
        } catch (Exception e) {
            model.addAttribute("Errors", e);
            return "errors";
        }
    }

    @PostMapping("/save/{bookingId}")
    public String savePayment(@PathVariable("bookingId") int bookingId, Payment payment, Model model) {
        try {

            Booking booking = bookingService.findBookingById(bookingId);
            payment.setBooking(booking);
            paymentService.createPayment(payment);
            bookingService.updatePaymentStatus(booking.getBookingId());
            return "redirect:/payments/showAdminPaymentList";
        } catch (Exception e) {
            model.addAttribute("Errors", e);
            return "errors";
        }
    }
    private Logger log = LoggerFactory.getLogger(getClass());
    public static final String PAYPAL_SUCCESS_URL = "payments/pay/success";
    public static final String PAYPAL_CANCEL_URL = "payments/pay/cancel";
    public int GB_BookingId;
    @GetMapping("/bookingDetail/{bookingId}")
    public String getBookingDetails(@PathVariable("bookingId") int bookingId, Model model) {
        try {
            Booking booking = bookingService.findBookingById(bookingId);
            GB_BookingId = booking.getBookingId();
            model.addAttribute("booking", booking);
            return "Paypal/bookingDetail";
        } catch (Exception e) {
            model.addAttribute("Errors", e);
            return "errors";
        }
    }

    @PostMapping("/pay")
    public String payWithPaypal(HttpServletRequest request, Model model) {
        try {
            int bookingId = GB_BookingId;
            Booking booking = bookingService.findBookingById(bookingId);
            double totalPrice = booking.getTotalPrice();

            BigDecimal totalPriceBD = new BigDecimal(totalPrice);
            BigDecimal conversionRate = new BigDecimal("23000"); // Example conversion rate
            BigDecimal result = totalPriceBD.divide(conversionRate, 2, RoundingMode.HALF_UP);
            double roundedResult = result.doubleValue();

            String cancelUrl = URLUtils.getBaseURl(request) +"/" + PAYPAL_CANCEL_URL;
            String successUrl = URLUtils.getBaseURl(request) +"/" + PAYPAL_SUCCESS_URL;

            com.paypal.api.payments.Payment payment = PaypalService.createPayment(
                    roundedResult,
                    "USD",
                    PaypalPaymentMethod.paypal,
                    PaypalPaymentIntent.sale,
                    "Payment for booking ID: " + bookingId,
                    cancelUrl,
                    successUrl);

            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return "redirect:" + links.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            log.error("Error while processing PayPal payment: " + e.getMessage());
            model.addAttribute("Errors", e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/pay/cancel")
    public String cancelPay() {
        return "Paypal/cancel";
    }

    @GetMapping("/pay/success")
    public String successPay(@RequestParam("paymentId") String paymentId,
                             @RequestParam("PayerID") String payerId,
                             Model model) {
        try {
            com.paypal.api.payments.Payment payment = PaypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                Booking booking = bookingService.findBookingById(GB_BookingId);

                com.example.booking.entity.Payment newPayment = new com.example.booking.entity.Payment();
                newPayment.setBooking(booking);
                newPayment.setPaymentType(paymentTypeService.getPaymentTypeById(1));
                newPayment.setTotalPayment(booking.getTotalPrice());
                newPayment.setPaymentTime(LocalDate.now().toString());
                paymentService.createPayment(newPayment);


                bookingService.updatePaymentStatus(booking.getBookingId());

                model.addAttribute("payment", newPayment);
                return "Paypal/success";
            }
        } catch (PayPalRESTException e) {
            log.error("Error while processing PayPal payment: " + e.getMessage());
            model.addAttribute("Errors", e.getMessage());
        }
        return "redirect:/";
    }


}
