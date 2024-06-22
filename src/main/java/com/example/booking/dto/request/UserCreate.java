package com.example.booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreate {
    private String username;
    private String email;
    private String password;
    private String avatar;
    private int role = 1;
}
