package com.example.mc_country.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityDto implements Serializable{
    private UUID id;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    private String title;
    private Integer countryId;
}
