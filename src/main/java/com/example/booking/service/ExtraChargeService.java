package com.example.booking.service;

import com.example.booking.entity.ExtraCharge;
import com.example.booking.entity.Payment;
import com.example.booking.repository.ExtraChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExtraChargeService {
    private final ExtraChargeRepository extraChargeRepository;
    private final PaymentService paymentService;

    @Autowired
    public ExtraChargeService(ExtraChargeRepository extraChargeRepository, PaymentService paymentService) {
        this.extraChargeRepository = extraChargeRepository;
        this.paymentService = paymentService;
    }

    public ExtraCharge createExtraCharge(ExtraCharge extraCharge, int paymentId){
        ExtraCharge newExtraCharge = new ExtraCharge();
        Payment existingPayment = paymentService.checkPayment(paymentId);
        if (existingPayment == null){
            throw new RuntimeException("Payment not found");
        }
        newExtraCharge.setPayExtra(extraCharge.getPayExtra());
        newExtraCharge.setReason(extraCharge.getReason());
        newExtraCharge.setPayment(existingPayment);
        extraChargeRepository.save(newExtraCharge);
        return newExtraCharge;
    }
    public ExtraCharge checkExtraCharge(int extraChargeId){
        return extraChargeRepository.findById(extraChargeId)
                .orElseThrow(() -> new RuntimeException("ExtraCharge not found"));
    }
    public ExtraCharge updateExtraCharge(ExtraCharge extraCharge){
        ExtraCharge existingExtraCharge = checkExtraCharge(extraCharge.getExtraChargeId());
        existingExtraCharge.setPayExtra(extraCharge.getPayExtra());
        existingExtraCharge.setReason(extraCharge.getReason());
        extraChargeRepository.save(existingExtraCharge);
        return existingExtraCharge;
    }

}
