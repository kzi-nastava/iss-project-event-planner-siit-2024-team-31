package com.example.eventplanner.model.event;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "event_attendances")
@Getter
@Setter
public class EventAttendance extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "check_in_time")
    private Instant checkInTime;

    @Column(name = "check_out_time")
    private Instant checkOutTime;

    @OneToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @Column(name = "attendance_duration_minutes")
    private Integer attendanceDurationMinutes;
}
