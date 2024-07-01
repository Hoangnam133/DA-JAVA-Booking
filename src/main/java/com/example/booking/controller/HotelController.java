package com.example.booking.controller;

import com.example.booking.entity.Hotel;
import com.example.booking.service.HandleImageService;
import com.example.booking.service.HotelService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final HandleImageService handleImageService;

    @Autowired
    public HotelController(HotelService hotelService, HandleImageService handleImageService) {
        this.hotelService = hotelService;
        this.handleImageService = handleImageService;
    }


    @GetMapping("/homeAdmin")
    public String getHotel(Model model) {
        List<Hotel> hotels = hotelService.getAllHotels();
        model.addAttribute("hotels", hotels);
        return "Hotels/home";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("hotel", new Hotel());
        return "Hotels/create";
    }

    @PostMapping("/save")
    public String saveAddForm(@Valid @ModelAttribute("hotel") Hotel hotel,
                              BindingResult bindingResult,
                              Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "Hotels/create";
        }

        hotelService.hotelCreate(hotel);
        return "redirect:/hotels/homeAdmin";
    }

    @GetMapping("/edit/{hotelId}")
    public String showEditForm(@PathVariable int hotelId, Model model) {
        try {
            Hotel existingHotel = hotelService.findHotelById(hotelId);
            model.addAttribute("hotel", existingHotel);
            return "Hotels/update";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Hotel not found");
            return "errorPage";
        }
    }

    @PostMapping("/saveEdit/{hotelId}")
    public String saveEditForm(@PathVariable int hotelId,
                               @RequestParam(value = "imageFile") MultipartFile imageFile,
                               @RequestParam(value = "imageFile1") MultipartFile imageFile1,
                               @RequestParam(value = "imageFile2") MultipartFile imageFile2,
                               @ModelAttribute("hotel") Hotel hotel,
                               Model model) {
        try {
            // Retrieve existing hotel data
            Hotel existingHotel = hotelService.findHotelById(hotelId);
            if (existingHotel == null) {
                model.addAttribute("errorMessage", "Hotel not found");
                return "errorPage";  // Handle case where hotel is not found
            }

            // Update hotel images if provided
            updateHotelImages(imageFile, imageFile1, imageFile2, existingHotel);

            // Update other hotel details if needed
            updateHotelDetails(hotel, existingHotel);

            // Save updated hotel details
            hotelService.hotelUpdate(existingHotel);

            // Redirect to admin home after successful update
            return "redirect:/hotels/homeAdmin";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error saving images: " + e.getMessage());
            return "errorPage";  // Handle image saving error
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            model.addAttribute("errorMessage", "Internal Server Error");
            return "errorPage";  // Generic error handling for unexpected exceptions
        }
    }

    private void updateHotelImages(MultipartFile imageFile, MultipartFile imageFile1, MultipartFile imageFile2, Hotel existingHotel) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String storedFilePath = handleImageService.saveImage(imageFile);
            existingHotel.setHotelImage1(storedFilePath);
        }

        if (imageFile1 != null && !imageFile1.isEmpty()) {
            String storedFilePath1 = handleImageService.saveImage(imageFile1);
            existingHotel.setHotelImage2(storedFilePath1);
        }

        if (imageFile2 != null && !imageFile2.isEmpty()) {
            String storedFilePath2 = handleImageService.saveImage(imageFile2);
            existingHotel.setHotelImage3(storedFilePath2);
        }
    }

    private void updateHotelDetails(Hotel hotel, Hotel existingHotel) {
        existingHotel.setHotelName(hotel.getHotelName());
        existingHotel.setHotelPhone(hotel.getHotelPhone());
        existingHotel.setHotelAddress(hotel.getHotelAddress());
        existingHotel.setHotelStatus(hotel.isHotelStatus());
    }





}
