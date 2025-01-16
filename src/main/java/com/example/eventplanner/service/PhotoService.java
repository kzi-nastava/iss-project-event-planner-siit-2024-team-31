package com.example.eventplanner.service;

import com.example.eventplanner.dto.PhotoDto;
import com.example.eventplanner.model.Photo;
import com.example.eventplanner.repository.PhotoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    public Photo createPhoto(PhotoDto PhotoDto) {
        return null;
    }
}
