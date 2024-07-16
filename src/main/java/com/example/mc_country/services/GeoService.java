package com.example.mc_country.services;

import com.example.mc_country.model.City;
import com.example.mc_country.model.Country;

import java.util.List;

public interface GeoService {
    List<Country> getCountries();
    List<City> getCities(String countryId);
    String uploadData();
}
