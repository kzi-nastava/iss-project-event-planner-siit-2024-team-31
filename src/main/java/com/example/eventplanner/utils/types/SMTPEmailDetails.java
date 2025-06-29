package com.example.eventplanner.utils.types;

import jakarta.annotation.Nullable;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SMTPEmailDetails {

    @Nullable
    //Nullable because we have 1 sender and it sets by program
    private String from;

    @NonNull
    private String to;

    @NonNull
    private String subject;

    @NonNull
    private String body;

    @Nullable
    private String attachments;
}
