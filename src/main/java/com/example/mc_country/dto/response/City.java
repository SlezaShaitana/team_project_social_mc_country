package com.example.mc_country.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class City implements Serializable {
    private String id;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    private String title;
    private CountryId countryId;
}
