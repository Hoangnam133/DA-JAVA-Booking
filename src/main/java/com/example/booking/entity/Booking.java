package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;
    private boolean paymentStatus;
    private boolean cancelStatus;
    private String cancellationReason;
    private boolean checkInStatus;
    private String pin;
    @OneToOne(mappedBy = "booking")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "roomId")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;


}