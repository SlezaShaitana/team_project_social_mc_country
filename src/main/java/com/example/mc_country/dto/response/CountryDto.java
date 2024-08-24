package com.example.mc_country.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryDto implements Serializable{
    private Integer id;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    private String title;
    private List<String> cities;

}
