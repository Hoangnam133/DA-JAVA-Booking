package com.example.booking.controller;

import com.example.booking.entity.Hotel;
import com.example.booking.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
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
                               @RequestParam("imageFile") MultipartFile imageFile,
                               @RequestParam("imageFile1") MultipartFile imageFile1,
                               @RequestParam("imageFile2") MultipartFile imageFile2,
                               @Valid Hotel hotel,
                               BindingResult result,
                               Model model) throws IOException {
        if (result.hasErrors()) {
            var errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            hotel.setHotelId(hotelId);
            return "Hotels/update";
        }
        try{
            hotel = hotelService.findHotelById(hotelId);
            if (!imageFile.isEmpty()) {
                String storedFileName = handleImage.saveImage(imageFile);
                hotel.setHotelImage1(storedFileName);
            }

            if (!imageFile1.isEmpty()) {
                String storedFileName1 = handleImage.saveImage(imageFile1);
                hotel.setHotelImage2(storedFileName1);
            }

            if (!imageFile2.isEmpty()) {
                String storedFileName2 = handleImage.saveImage(imageFile2);
                hotel.setHotelImage3(storedFileName2);
            }
            hotelService.hotelUpdate(hotel);
            return "redirect:/hotels/homeAdmin";
        }catch (Exception e){
            model.addAttribute("errorMessage", "Hotel not found");
            return "errorPage";
        }
    }


}
