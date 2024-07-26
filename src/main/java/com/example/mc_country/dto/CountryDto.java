package com.example.mc_country.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
public class CountryDto implements Serializable {
    private UUID id;
    private boolean isDeleted;
    private String title;
    private List<String> cities;

}
