package com.example.mc_country.dto.HhApi;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;


@Data
public class CountryDataFromHhApi {
    private Long id;
    @JsonAlias({"parent_id"})
    private Long parentId;
    private String name;
    private List<CountryDataFromHhApi> areas;

}
