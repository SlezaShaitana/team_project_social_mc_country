package com.example.mc_country.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CountryDto {
    private UUID id;
    private boolean isDeleted;
    private String title;
    private List<String> cities = new ArrayList<>();

}
