package com.example.mc_country.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CityDto {
    private UUID id;
    private boolean isDeleted;
    private String title;
    private UUID countryId;
}
