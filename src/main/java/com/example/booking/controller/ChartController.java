package com.example.booking.controller;

import com.example.booking.service.ExtraChargeService;
import com.example.booking.service.PaymentService;
import com.example.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChartController {
    private final PaymentService paymentService;
    private final ExtraChargeService extraChargeService;
    private final UserService userService;

    @Autowired
    public ChartController(PaymentService paymentService, ExtraChargeService extraChargeService, UserService userService) {
        this.paymentService = paymentService;
        this.extraChargeService = extraChargeService;
        this.userService = userService;
    }

    @GetMapping("/revenue-statistics")
    public String showRevenueStatistics(Model model) {
        double totalPayments = paymentService.getTotalPayments();
        double totalExtraCharges = extraChargeService.getTotalExtraCharges();
        int totalLogin = userService.getTotalCountLogin();
        model.addAttribute("totalPayments", totalPayments);
        model.addAttribute("totalExtraCharges", totalExtraCharges);
        model.addAttribute("totalLogin", totalLogin);
        return "Chart/chartTotalRevenue";
    }
}
