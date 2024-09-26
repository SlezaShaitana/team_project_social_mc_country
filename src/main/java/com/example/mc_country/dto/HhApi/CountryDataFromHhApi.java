package com.example.mc_country.dto.HhApi;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryDataFromHhApi {
    private Long id;
    @JsonAlias({"parent_id"})
    private Long parentId;
    private String name;
    private List<CountryDataFromHhApi> areas;

}
