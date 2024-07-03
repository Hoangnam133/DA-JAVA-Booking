package com.example.booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;
    private String reviewTime;
    @NotBlank(message = "Comments cannot be empty")
    private String comment;
    private String imageReview1;
    private String imageReview2;
    private boolean commentStatus;
    @OneToOne
    @JoinColumn(name = "paymentId")
    private Payment payment;
}