package com.example.mc_country.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Country {
    private UUID id;
    private boolean isDeleted;
    private String title;
    private List<City> cities;

}
