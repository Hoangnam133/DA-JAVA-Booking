package com.example.booking.controller;

import com.example.booking.entity.Hotel;
import com.example.booking.entity.Room;
import com.example.booking.entity.User;
import com.example.booking.service.HotelService;
import com.example.booking.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/hotels")

public class HotelController {

    private final HotelService hotelService;
    public HotelController(HotelService hotelService){
        this.hotelService = hotelService;
    }
    @GetMapping("/home")
    public String getHotel(Model model){
        List<Hotel> hotels = hotelService.getAllHotels();
        model.addAttribute("hotels",hotels);
        return "Hotels/home";
    }
    @GetMapping("/add")
    public String showAddFrom(Model model){
        model.addAttribute("hotel", new Hotel());
        return "Hotels/add";
    }
    @PostMapping("/save")
    public String saveAddFrom(@Valid @ModelAttribute("hotel") Hotel hotel,
                              @NotNull BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "Hotels/add";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            User currentUser = (User) authentication.getPrincipal();
            Hotel existingHotel = hotelService.findHotelByUserId(currentUser.getId());
            if (existingHotel != null) {
                model.addAttribute("errorMessage", "You can only create one hotel.");
                return "Hotels/add";
            }
            hotel.setUser(currentUser);
            try {
                hotelService.hotelCreate(hotel);
                return "redirect:/hotels";
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "Hotels/add";
            }
        } else {
            model.addAttribute("errorMessage", "You need to be logged in to create a hotel.");
            return "errorPage";
        }
    }
    @GetMapping("/edit/{hotelId}")
    public String showEditFrom(@PathVariable int hotelId, Model model){
        try {
            Hotel existingHotel = hotelService.findHotelById(hotelId);
            model.addAttribute("hotel", existingHotel);
            return "Hotels/edit";
        }catch (Exception e){
            model.addAttribute("errorMessage", "Hotel not found");
            return "errorPage";
        }
    }
    @PostMapping("/saveEdit/{hotelId}")
    public String saveEditForm(@PathVariable int hotelId,
                               @Valid Hotel hotel, BindingResult result, Model model){
        if (result.hasErrors()) {
            model.addAttribute("hotelId", hotelId);
            return "Hotels/edit";
        }
        try {
            hotel.setHotelId(hotelId);
            hotelService.hotelUpdate(hotel);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", "Room not found");
            return "errorPage";
        }
        return "redirect:/home";
    }



}
