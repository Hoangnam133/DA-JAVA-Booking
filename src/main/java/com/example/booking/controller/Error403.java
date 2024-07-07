package com.example.booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Error403 {
    @GetMapping("Error403")
    public String error403() {
        return "403";
    }
}