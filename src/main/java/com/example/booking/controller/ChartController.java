package com.example.booking.controller;

import com.example.booking.service.ExtraChargeService;
import com.example.booking.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChartController {
    private final PaymentService paymentService;
    private final ExtraChargeService extraChargeService;

    @Autowired
    public ChartController(PaymentService paymentService, ExtraChargeService extraChargeService) {
        this.paymentService = paymentService;
        this.extraChargeService = extraChargeService;
    }

    @GetMapping("/revenue-statistics")
    public String showRevenueStatistics(Model model) {
        double totalPayments = paymentService.getTotalPayments();
        double totalExtraCharges = extraChargeService.getTotalExtraCharges();

        model.addAttribute("totalPayments", totalPayments);
        model.addAttribute("totalExtraCharges", totalExtraCharges);

        return "Chart/chartTotalRevenue";
    }
}
