package com.example.booking.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int hotelId;

    @Column(length = 255)
    @NotBlank(message = "hotelName is required")
    @Size(min = 1, max = 255, message = "hotelName must be between 1 and 255 characters")
    private String hotelName;

    @Column(length = 10)
    @Length(min = 10, max = 10, message = "Phone must be 10 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Phone must be number")
    private String hotelPhone;

    @NotBlank(message = "Hotel address is required")
    @Size(min = 20,max = 255, message = "Hotel address must be between 20 and 255 characters")
    private String hotelAddress;
    private String hotelImage1;
    private String hotelImage2;
    private String hotelImage3;
    private boolean hotelStatus;
    @OneToMany(mappedBy = "hotel")
    private List<Room> rooms;
}