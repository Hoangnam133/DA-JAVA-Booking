package com.example.booking.controller;

import com.example.booking.config.PaypalPaymentIntent;
import com.example.booking.config.PaypalPaymentMethod;
import com.example.booking.entity.Booking;
import com.example.booking.entity.Payment;
import com.example.booking.entity.User;
import com.example.booking.service.*;
import com.example.booking.util.URLUtils;
import com.paypal.api.payments.Links;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final PaymentTypeService paymentTypeService;
    private final UserService userService;
    private final MailService mailService;


    @Autowired
    public PaymentController(PaymentService paymentService, BookingService bookingService,
                             PaymentTypeService paymentTypeService,
                             UserService userService,
                             MailService mailService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.paymentTypeService = paymentTypeService;
        this.userService = userService;
        this.mailService = mailService;
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
    public String savePayment(@PathVariable("bookingId") int bookingId, Payment payment,
                              Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String username = userDetails.getUsername();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Booking booking = bookingService.findBookingById(bookingId);
            payment.setBooking(booking);
            payment.setPaymentPin(bookingService.generateRandomString());
            paymentService.createPayment(payment);
            sendPaymentToEmail(payment,user);

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
                             Model model
            ,@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String username = userDetails.getUsername();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            com.paypal.api.payments.Payment payment = PaypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                Booking booking = bookingService.findBookingById(GB_BookingId);

                com.example.booking.entity.Payment newPayment = new com.example.booking.entity.Payment();
                newPayment.setBooking(booking);
                newPayment.setPaymentType(paymentTypeService.getPaymentTypeById(1));
                newPayment.setTotalPayment(booking.getTotalPrice());
                newPayment.setPaymentTime(LocalDate.now().toString());
                newPayment.setPaymentPin(bookingService.generateRandomString());

                paymentService.createPayment(newPayment);
                sendPaymentToEmail(newPayment,user);
                bookingService.updateCheckInStatus(booking.getBookingId());
                bookingService.updatePaymentStatus(booking.getBookingId());
                model.addAttribute("payment", newPayment);
                return "Paypal/success";
            }
        } catch (PayPalRESTException e) {
            log.error("Error while processing PayPal payment: " + e.getMessage());
            model.addAttribute("Errors", e.getMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/";
    }
    @GetMapping("/listPaymentOfUser")
    public String getAllPaymentOfUser(@AuthenticationPrincipal UserDetails userDetails, Model model)
    {
        try {
            String username = userDetails.getUsername();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("payments",paymentService.listOfUser(user.getId()));
            return "Payments/listOfUser";
        }catch (Exception e){
            model.addAttribute("Errors",e);
            return "errors";
        }
    }
    public void sendPaymentToEmail(Payment payment, User user) throws MessagingException {
        String subject = "Payment Confirmation";
        String htmlBody = "<div style='border: 3px solid #ccc; border-radius: 10px; overflow: hidden;'>" +
                "<div style='background-color: #f8f9fa; padding: 20px;'>" +
                "<h2 style='color: #007bff; margin-bottom: 10px;'>Payment Confirmation</h2>" +
                "<p>Dear " + user.getUsername() + ",</p>" +
                "<p>Your pay has been confirmed.</p>" +
                "<h3>Pay Details:</h3>" +
                "<ul style='list-style-type: none; padding-left: 0;'>" +
                "<li><strong>Payment Time:</strong> " + payment.getPaymentTime() + "</li>" +
                "<li><strong>Total Price:</strong> " + payment.getTotalPayment() + "</li>" +
                "<li><strong>Booking ID:</strong> $" + payment.getBooking().getBookingId() + "</li>" +
                "<li><strong>Payment ID:</strong> " + payment.getPaymentId() + "</li>" +
                "<li><strong>Payment Pin:</strong> " + payment.getPaymentPin() + "</li>" +
                "</ul>" +
                "<p>Thank you for booking with us!</p>" +
                "</div>" +
                "</div>";
        mailService.sendMail(user.getEmail(), subject, htmlBody);
    }

}
