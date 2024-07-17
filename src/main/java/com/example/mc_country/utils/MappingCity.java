package com.example.mc_country.utils;

import com.example.mc_country.dto.CityDto;
import com.example.mc_country.entity.City;

import java.util.UUID;

public class MappingCity {
    public static CityDto mapToDto(City city){
        CityDto cityDto = new CityDto();
        cityDto.setId(UUID.fromString(city.getId()));
        cityDto.setDeleted(city.isDeleted());
        cityDto.setTitle(city.getTitle());
        cityDto.setCountryId(UUID.fromString(city.getCountry().getId()));
        return cityDto;
    }
}
