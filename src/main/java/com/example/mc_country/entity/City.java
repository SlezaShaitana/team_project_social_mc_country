package com.example.mc_country.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "city")
public class City {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "city_title", nullable = false)
    private String title;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id")
    private Country country;

}
