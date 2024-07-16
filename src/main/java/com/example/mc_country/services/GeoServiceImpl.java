package com.example.mc_country.services;

import com.example.mc_country.model.City;
import com.example.mc_country.model.Country;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeoServiceImpl implements GeoService{
    @Override
    public List<Country> getCountries() {
        return List.of();
    }

    @Override
    public List<City> getCities(String countryId) {
        return List.of();
    }

    @Override
    public String uploadData() {
        return "";
    }
}
