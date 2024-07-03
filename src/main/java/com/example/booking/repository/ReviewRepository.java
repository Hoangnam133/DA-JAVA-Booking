package com.example.booking.repository;

import com.example.booking.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {
    Review findByPayment_PaymentIdAndPayment_PaymentPin(int paymentId, String paymentPin);
}
