package ru.practicum.shareit.booking;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @Column(name = "item_id")
    private Integer item;
    @Column(name = "booker_id")
    private Integer booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

}
