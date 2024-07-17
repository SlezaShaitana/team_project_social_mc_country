package com.example.mc_country.utils;


import com.example.mc_country.dto.CountryDto;
import com.example.mc_country.entity.City;
import com.example.mc_country.entity.Country;

import java.util.UUID;


public class MappingCountry {
    public static CountryDto mapToDto(Country country){
        CountryDto countryDto = new CountryDto();
        countryDto.setId(UUID.fromString(country.getId()));
        countryDto.setDeleted(country.isDeleted());
        countryDto.setTitle(country.getTitle());
        countryDto.setCities(
                country.getCities()
                        .stream()
                        .map(City::getTitle)
                        .toList()
        );
        return countryDto;
    }
}
