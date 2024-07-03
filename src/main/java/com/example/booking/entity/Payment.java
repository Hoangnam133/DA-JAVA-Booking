package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    private String paymentTime;
    private double totalPayment;

    private String paymentPin;

    @ManyToOne // Quan hệ nhiều một (many-to-one) với PaymentType
    @JoinColumn(name = "paymentTypeId")
    private PaymentType paymentType;

    @OneToOne
    @JoinColumn(name = "bookingId")
    private Booking booking;

    @OneToOne(mappedBy = "payment")
    private Review review;

    @OneToMany(mappedBy = "payment")
    private List<ExtraCharge> extraCharges;
}
