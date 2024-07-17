package com.example.mc_country.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "country")
public class Country {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "country_title", nullable = false)
    private String title;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "country")
    private List<City> cities;

}
