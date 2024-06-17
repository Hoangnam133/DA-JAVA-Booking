package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
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
    private Date paymentTime;
    private double totalPayment;
    @OneToOne
    @JoinColumn(name = "bookingId")
    private Booking booking;

    @OneToOne
    @JoinColumn(name = "paymentTypeId")
    private PaymentType paymentType;

    @OneToOne(mappedBy = "payment")
    private Review review;

    @OneToMany(mappedBy = "payment")
    private List<ExtraCharge> extraCharges;

}