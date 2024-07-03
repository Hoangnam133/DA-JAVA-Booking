package com.example.booking.service;

import com.example.booking.entity.Payment;
import com.example.booking.entity.Review;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.PaymentRepository;
import com.example.booking.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, PaymentRepository paymentRepository) {
        this.reviewRepository = reviewRepository;
        this.paymentRepository = paymentRepository;
    }
    public void createReview(Review review) {
        reviewRepository.save(review);
    }
    public Review findReviewById(int id) {
        return reviewRepository.findById(id)
                .orElse(null);
    }
    public Page<Review> findAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }
    public Review updateReview(Review review) {
        return reviewRepository.save(review);
    }
    public Review checkExistingReview(String pin, int paymentId) {
         return reviewRepository.findByPayment_PaymentIdAndPayment_PaymentPin(paymentId, pin);

    }


}
