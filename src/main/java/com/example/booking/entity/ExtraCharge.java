package com.example.booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\d\\s]+$", message = "Field must contain only letters, numbers, and spaces")
    @Column(nullable = false)
    private String reason;
    @Column(nullable = false)
    @Min(value = 500, message = "Pay extra must be at least 500")
    private double payExtra;
    private String extraChargeDate;
    private boolean isDelete;
    @ManyToOne
    @JoinColumn(name = "PaymentId")
    private Payment payment;

}
