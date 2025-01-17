package com.example.mc_country.services;

import com.example.mc_country.dto.CityDto;
import com.example.mc_country.dto.CountryDto;

import java.util.List;
import java.util.UUID;

public interface GeoService {
    List<CountryDto> getAllCountries();
    List<CityDto> getCitiesOfCountry(UUID countryId);
    String uploadData();
}
