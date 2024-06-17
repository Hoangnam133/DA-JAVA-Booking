package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "ExtraCharges")
public class ExtraCharge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int extraChargeId;
    private String reason;
    private double payExtra;

    @ManyToOne
    @JoinColumn(name = "PaymentId")
    private Payment payment;

}
