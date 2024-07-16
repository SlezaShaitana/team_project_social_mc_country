package com.example.mc_country.model;

import lombok.Data;

import java.util.UUID;

@Data
public class City {
    private UUID id;
    private boolean isDeleted;
    private String title;
    private Country country;
}
