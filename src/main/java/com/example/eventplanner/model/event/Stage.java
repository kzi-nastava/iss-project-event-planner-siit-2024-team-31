package com.example.eventplanner.model.event;

import jakarta.persistence.Column;

import java.util.Date;


public class Stage {


    // Category Enum class 3.1
    @Column(name = "category")
    private String category;

    @Column(name = "name")
    private String name;

    // Number of guest for this service
    @Column(name = "maximux_number_of_guests")
    private int maxNumberOfGuests;

    @Column(name = "description")
    private String description;

    @Column(name = "issue")
    private String issue;

    @Column(name = "price")
    private int price;

    @Column(name = "discount")
    private double discount;

    @Column(name = "photo")
    private String photo;

    @Column(name = "type_of_events_where_its_applicable")
    private double typeOfEventsWhereItsApplicable;

    @Column(name = "is_active_for_OD")
    private boolean isActiveForOD = false;

    @Column(name = "is_active_for_users")
    private boolean isActiveForUsers = false;

    // Продолжительность услуги (например, 1 ч, 2 ч, 15 мин)
    // или минимальное и максимальное время для оказания услуги
    // (например, минимум 1 ч, максимум 5 ч).
    @Column(name = "duration_of_service")
    private Date durationOfService;

    //  Сроки бронирования (за сколько времени
    //  до события можно забронировать услугу)
    //  и отмены (время до события, когда можно отменить бронирование).
    @Column(name = "avaliable_for_ordering_from_to")
    private Date avaliableForOrderingFromTo;

    @Column(name = "cancelation")
    private Date cancelation;

    //Способы подтверждения бронирования:
    // ■ Автоматическое — для услуг с фиксированным временем.
    // ■ Ручное — для услуг с фиксированным временем или услуг с гибким временем.

}
