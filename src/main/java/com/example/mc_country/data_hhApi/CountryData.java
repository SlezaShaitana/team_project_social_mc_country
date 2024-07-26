package com.example.mc_country.data_hhApi;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;


@Data
public class CountryData {
    private Long id;
    @JsonAlias({"parent_id"})
    private Long parentId;
    private String name;
    private List<CountryData> areas;

}
