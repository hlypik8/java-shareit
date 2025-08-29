package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requests")
@Data
@Setter
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "description")
    private String description;

    @Column(name = "requester_id")
    private Integer requester;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();

    @OneToMany(mappedBy = "request",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();
}
