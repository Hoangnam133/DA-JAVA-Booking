package com.example.booking.eRole;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Role {
    ADMIN(1),
    USER(2),
    EMPLOYEE(3);
    public final int value;
}
