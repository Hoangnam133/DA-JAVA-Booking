package com.example.booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roomId;
    @Column(length = 50)
    @NotBlank(message = "roomNumber is required")
    @Size(min = 1, max = 50, message = "roomNumber must be between 1 and 50 characters")
    private String roomNumber;
    @NotBlank(message = "Description is required")
    private String description;
    @NotNull(message = "Price is required")
    @Min(value = 1000, message = "Price must be greater than 1000 VND")
    private Double price;
    private String roomImage1;
    private String roomImage2;
    private boolean roomStatus;
    @ManyToOne
    @JoinColumn(name = "hotelId")
    private Hotel hotel;
    @ManyToOne
    @JoinColumn(name = "roomTypeId")
    private RoomType roomType;
    @OneToMany(mappedBy = "room")
    private List<Booking> bookings;

}