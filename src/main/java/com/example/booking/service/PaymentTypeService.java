package com.example.booking.service;

import com.example.booking.entity.PaymentType;
import com.example.booking.repository.PaymentTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentTypeService {
    private final PaymentTypeRepository paymentTypeRepository;
    public PaymentTypeService(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }
    public List<PaymentType> getAllPaymentTypes() {
        return paymentTypeRepository.findAll();
    }
    public PaymentType getPaymentTypeById(int id) {
        return paymentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentType not found with id: " + id));
    }
}
