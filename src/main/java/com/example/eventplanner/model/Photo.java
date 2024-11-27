package com.example.eventplanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "photos")
@NoArgsConstructor
public class Photo extends EntityBase{

      //  @Column(nullable = false)
        @Column(nullable = true)
        private byte[] binaryPhoto;

        @Column
        private Date uploadDate = new Date();

        public Photo(byte[] binaryPhoto) {
            this.binaryPhoto = binaryPhoto;
        }
    }

