package com.example.eventplanner.service;

import com.example.eventplanner.dto.PhotoDto;
import com.example.eventplanner.model.Photo;
import com.example.eventplanner.repository.PhotoRepository;
import org.springframework.stereotype.Service;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public Photo createPhoto(PhotoDto PhotoDto) {
        Photo photo = new Photo(PhotoDto.getPhoto());
        photoRepository.saveAndFlush(photo);
        return photo;
    }
}
