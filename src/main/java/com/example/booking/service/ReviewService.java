package com.example.booking.service;

import com.example.booking.entity.Payment;
import com.example.booking.entity.Review;
import com.example.booking.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PaymentService paymentService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, PaymentService paymentService) {
        this.reviewRepository = reviewRepository;
        this.paymentService = paymentService;
    }

    public Review createReview(Review review, int paymentId){
        Review newReview = new Review();
        Payment existingPayment = paymentService.checkPayment(paymentId);
        if (existingPayment == null){
            throw new RuntimeException("Payment not found");
        }
        if(reviewRepository.findByPayment_PaymentId(paymentId) != null){
            throw new RuntimeException("This invoice has been evaluated");
        }
        newReview.setReviewTime(new Date());
        newReview.setComment(review.getComment());
        newReview.setPayment(existingPayment);
        newReview.setCommentStatus(false);
        reviewRepository.save(newReview);
        return review;
    }
    public Review reviewApproved(Review review){
        Review existingReview = reviewRepository.findById(review.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        existingReview.setCommentStatus(true);
        return reviewRepository.save(existingReview);
    }
    public List<Review> ShowReviewList(){
        return reviewRepository.findAll();
    }

}
