package com.example.eventplanner.dto;

import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoDto {
    private byte[] photo;
}
