package com.example.mc_country.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CityDto implements Serializable {
    private UUID id;
    private boolean isDeleted;
    private String title;
    private UUID countryId;
}
