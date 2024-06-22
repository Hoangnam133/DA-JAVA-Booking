package com.example.booking.controller;

import com.example.booking.entity.Hotel;
import com.example.booking.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/hotels")

public class HotelController {
// ok ok
    @Autowired
    private HotelService hotelService;
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("hotel", new Hotel());
        return "Hotels/create";
    }

    @PostMapping("/save")
    public String createHotel(@ModelAttribute("hotel") Hotel hotel) {
        try {
            Hotel createdHotel = hotelService.hotelCreate(hotel);
            return "redirect:/hotels/home";
        } catch (IllegalStateException e) {
            return "redirect:/hotels/create?error=" + e.getMessage();
        }
    }

    @GetMapping("/update/{hotelId}")
    public String showUpdateForm(@PathVariable("hotelId") int hotelId, Model model) {
        Hotel hotel = hotelService.findHotelById(hotelId);
        model.addAttribute("hotel", hotel);
        return "Hotels/update";
    }

    @PostMapping("/update")
    public String updateHotel(@ModelAttribute("hotel") Hotel hotel) {
        hotelService.hotelUpdate(hotel);
        return "redirect:/hotels/home";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        List<Hotel> hotels = hotelService.getAllHotels();
        model.addAttribute("hotels", hotels);
        return "Hotels/home";
    }
}
