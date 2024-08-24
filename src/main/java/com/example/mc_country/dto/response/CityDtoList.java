package com.example.mc_country.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CityDtoList {
    List<CityDto> cities = new ArrayList<>();
}
