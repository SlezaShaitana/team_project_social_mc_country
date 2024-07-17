package com.example.mc_country.services;

import com.example.mc_country.dto.CityDto;
import com.example.mc_country.dto.CountryDto;

import java.util.List;

public interface GeoService {
    List<CountryDto> getCountries();
    List<CityDto> getCities(String countryId);
    String uploadData();
}
