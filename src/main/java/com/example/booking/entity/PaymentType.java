package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "PaymentTypes")
public class PaymentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentTypeId;

    private String paymentTypeName;

    @OneToMany(mappedBy = "paymentType")
    private List<Payment> payments; // Quan hệ một nhiều (one-to-many) với Payment
}
