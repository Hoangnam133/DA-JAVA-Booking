package com.example.booking.service;

import com.example.booking.entity.ExtraCharge;
import com.example.booking.entity.Payment;
import com.example.booking.repository.ExtraChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtraChargeService {
    private final ExtraChargeRepository extraChargeRepository;


    @Autowired
    public ExtraChargeService(ExtraChargeRepository extraChargeRepository) {
        this.extraChargeRepository = extraChargeRepository;
    }
    public List<ExtraCharge> getExtraCharges() {
        return extraChargeRepository.findAll();
    }
    public Page<ExtraCharge> getExtraCharges(Pageable pageable) {
        return extraChargeRepository.findAll(pageable);
    }
    public void createExtraCharge(ExtraCharge extraCharge){
        extraChargeRepository.save(extraCharge);
    }
    public ExtraCharge checkExtraCharge(int extraChargeId){
        return extraChargeRepository.findById(extraChargeId)
                .orElseThrow(() -> new RuntimeException("ExtraCharge not found"));
    }
    public void updateExtraCharge(ExtraCharge extraCharge){
       extraChargeRepository.save(extraCharge);
    }
    public double getTotalExtraCharges() {
        return extraChargeRepository.findAll().stream().mapToDouble(ExtraCharge::getPayExtra).sum();
    }

}
