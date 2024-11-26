package com.example.eventplanner.model.company;

import com.example.eventplanner.model.EntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "company_photos")
@NoArgsConstructor
public class CompanyPhoto extends EntityBase{

        @Column(nullable = false)
        private byte[] binaryPhoto;

        @Column
        private Date uploadDate = new Date();

        public CompanyPhoto(byte[] binaryPhoto) {
            this.binaryPhoto = binaryPhoto;
        }
    }

