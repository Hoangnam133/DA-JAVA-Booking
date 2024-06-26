package com.example.booking.controller;

import com.example.booking.entity.ExtraCharge;
import com.example.booking.entity.Payment;
import com.example.booking.service.ExtraChargeService;
import com.example.booking.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/extraCharges")
public class ExtraChargeController {
    private final ExtraChargeService extraChargeService;
    private final PaymentService paymentService;
    public ExtraChargeController(ExtraChargeService extraChargeService, PaymentService paymentService) {
        this.extraChargeService = extraChargeService;
        this.paymentService = paymentService;
    }
    @GetMapping("showAdminExtraChargesList")
    public String listExtraCharges(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 6);
        Page<ExtraCharge> extraChargePage = extraChargeService.getExtraCharges(pageable);
        model.addAttribute("extraCharges", extraChargePage.getContent());
        model.addAttribute("totalPages", extraChargePage.getTotalPages());
        model.addAttribute("currentPage",page);
        return "ListOfAdmin/listExtraCharges";
    }
    @GetMapping("/add/{paymentId}")
    public String addExtraCharge(@PathVariable("paymentId") int paymentId, Model model) {
        try {
            Payment existingPayment = paymentService.checkPayment(paymentId);
            ExtraCharge extraCharge = new ExtraCharge();
            extraCharge.setPayment(existingPayment);
            model.addAttribute("extraCharge", extraCharge);
            model.addAttribute("payment", existingPayment);
            return "ExtraCharges/add";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "errorPage";
        }
    }

    @PostMapping("/save/{paymentId}")
    public String save(@PathVariable("paymentId") int paymentId, @Valid ExtraCharge extraCharge, BindingResult result, Model model) {
        Payment existingPayment = paymentService.checkPayment(paymentId);
        if (result.hasErrors()) {
            var errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            extraCharge.setPayment(existingPayment);
            return "ExtraCharges/add";
        }
        extraCharge.setDelete(false);
        extraCharge.setPayment(existingPayment);
        extraChargeService.createExtraCharge(extraCharge);
        return "redirect:/extraCharges/showAdminExtraChargesList";
    }

    @GetMapping("edit/{extraChargesId}")
    public String editExtraCharge(@PathVariable("extraChargesId") int extraChargeId, Model model) {
        try {
            ExtraCharge existingExtraCharges = extraChargeService.checkExtraCharge(extraChargeId);
            model.addAttribute("extraCharge", existingExtraCharges);
            return "ExtraCharges/edit";
        }catch (Exception e){
            model.addAttribute("errorMessage",e.getMessage());
            return "errorPage";
        }
    }
    @PostMapping("saveEdit/{extraChargesId}")
    public String saveEditExtraCharge(@PathVariable("extraChargesId") int extraChargeId, @Valid ExtraCharge extraCharge,
                                      BindingResult result, Model model){
        ExtraCharge existingExtraCharges = extraChargeService.checkExtraCharge(extraChargeId);
        if (result.hasErrors()) {
            var errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            extraCharge.setExtraChargeId(extraChargeId);
            extraCharge.setPayment(existingExtraCharges.getPayment());
            return "ExtraCharges/edit";
        }
        extraCharge.setPayment(existingExtraCharges.getPayment());
        extraChargeService.updateExtraCharge(extraCharge);
        return "redirect:/extraCharges/showAdminExtraChargesList";
    }
}
