package com.example.booking.controller;

import com.example.booking.entity.Payment;
import com.example.booking.entity.Review;
import com.example.booking.service.HandleImageService;
import com.example.booking.service.PaymentService;
import com.example.booking.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final PaymentService paymentService;
    @Autowired
    private HandleImageService handleImageService;
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
    public String saveAddForm(@Valid @ModelAttribute("review") Review review,
                              @RequestParam("reviewImage1File") MultipartFile reviewImage1File,
                              @RequestParam("reviewImage2File") MultipartFile reviewImage2File,
                              @NotNull BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("error", errors);
            return "Reviews/add";
        }

        try {
            review.setPayment(paymentService.checkPayment(checkPaymentId));
            LocalDate timeNow = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String conv = timeNow.format(formatter);
            review.setCommentStatus(true);
            review.setReviewTime(conv);

            if (!reviewImage1File.isEmpty()) {
                String image1Path = handleImageService.saveImage(reviewImage1File);
                review.setImageReview1(image1Path);
            }

            if (!reviewImage2File.isEmpty()) {
                String image2Path = handleImageService.saveImage(reviewImage2File);
                review.setImageReview2(image2Path);
            }

            reviewService.createReview(review);
            return "redirect:/reviews/list";
        } catch (IOException ex) {
            model.addAttribute("errors", new String[]{"Lỗi lưu ảnh đánh giá: " + ex.getMessage()});
            return "Reviews/add";
        }


    }
}
