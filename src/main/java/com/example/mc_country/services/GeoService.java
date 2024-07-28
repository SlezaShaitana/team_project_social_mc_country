package com.example.mc_country.services;

import com.example.mc_country.dto.response.CityDto;
import com.example.mc_country.dto.response.CountryDto;

import java.util.List;
import java.util.UUID;

public interface GeoService {
    List<CountryDto> getAllCountries();
    List<CityDto> getCitiesOfCountry(String countryId);
    String uploadData();
}
