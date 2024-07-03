package com.example.booking.controller;

import com.example.booking.entity.Payment;
import com.example.booking.entity.Review;
import com.example.booking.service.PaymentService;
import com.example.booking.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final PaymentService paymentService;
    public ReviewController(ReviewService reviewService, PaymentService paymentService) {
        this.reviewService = reviewService;
        this.paymentService = paymentService;
    }
    public int checkPaymentId;
    @GetMapping("/check")
    public String showCheckReview(@RequestParam(name = "paymentId", required = false) Integer paymentId,
                                  @RequestParam(name = "pin", required = false) String pin, Model model) {
        if (paymentId == null) {
            // Xử lý khi paymentId không được nhập
            model.addAttribute("error", "Mã thanh toán không được để trống.");
            return "Reviews/check";
        }

        // Tiếp tục xử lý khi paymentId có giá trị
        Payment payment = paymentService.getPaymentByIdAndPin(paymentId, pin);
        if (payment != null) {
            Review review = reviewService.checkExistingReview(pin, paymentId);
            if (review == null) {
                checkPaymentId = paymentId;
                return "redirect:/reviews/add";
            } else {
                model.addAttribute("error", "Phiếu thanh toán này đã được đánh giá rồi.");
                return "Reviews/check";
            }
        } else {
            model.addAttribute("error", "Mã thanh toán hoặc mã PIN không chính xác.");
            return "Reviews/check";
        }
    }


    @GetMapping("/list")
    public String getAllReview(@RequestParam(defaultValue = "0") int page, Model model){
        try{
            Pageable pageable = PageRequest.of(page, 10);
            Page<Review> reviewPage = reviewService.findAllReviews(pageable);
            model.addAttribute("reviews", reviewPage.getContent());
            model.addAttribute("totalPages", reviewPage.getTotalPages());
            model.addAttribute("currentPage", page);
            return "Reviews/list";
        }catch(Exception e){
            model.addAttribute("error", e.getMessage());
            return "errorPage";
        }
    }
    @GetMapping("/add")
    public String showAddFrom(@NotNull Model model){
        try {
            Review review = new Review();
            model.addAttribute("review",review);
            return "Reviews/add";
        }catch (Exception ex){
            model.addAttribute("error",ex.getMessage());
            return "Reviews/add";
        }
    }

    @PostMapping("/save")
    public String saveAddFrom(@Valid @ModelAttribute("review")  Review review, @NotNull BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("error", errors);
            return "Reviews/add";
        }
        review.setPayment(paymentService.checkPayment(checkPaymentId));
        reviewService.createReview(review);
        return "redirect:/reviews/list";
    }
}
