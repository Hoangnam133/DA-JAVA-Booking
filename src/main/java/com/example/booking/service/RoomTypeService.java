package com.example.booking.service;

import com.example.booking.entity.RoomType;
import com.example.booking.repository.RoomRepository;
import com.example.booking.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomTypeService {
    private final RoomTypeRepository roomTypeRepository;
    @Autowired
    public RoomTypeService(RoomTypeRepository roomTypeRepository){
        this.roomTypeRepository = roomTypeRepository;
    }
    public List<RoomType> getAll(){
        return roomTypeRepository.findAll();
    }

}
