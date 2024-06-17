package com.example.booking.repository;

import com.example.booking.entity.ExtraCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtraChargeRepository extends JpaRepository<ExtraCharge,Integer> {

}
