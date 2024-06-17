package com.example.booking.entity;

import jakarta.persistence.*;
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
    private String roomNumber;
    private String description;
    private double price;
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