package com.example.eventplanner.model.user;

import com.example.eventplanner.model.EntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.Date;


@Entity
@Setter
@Getter
@Table(name = "photos")
@NoArgsConstructor
public class Photo extends EntityBase {
    @Column(nullable = false)
    private byte[] binaryPhoto;

    @Column
    private Date uploadDate = new Date();

    public Photo(byte[] binaryPhoto) {
        this.binaryPhoto = binaryPhoto;
    }
}
