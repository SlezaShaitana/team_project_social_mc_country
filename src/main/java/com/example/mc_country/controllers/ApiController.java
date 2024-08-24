package com.example.mc_country.controllers;

import com.example.mc_country.dto.response.CityDto;
import com.example.mc_country.dto.response.CountryDto;
import com.example.mc_country.services.GeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/geo")
public class ApiController {

    private final GeoService geoService;

    @GetMapping("/country")
    public List<CountryDto> countries() {
        return geoService.getAllCountries();
    }

    @GetMapping("/country/{countryId}/city")
    public List<CityDto> cities(@PathVariable String countryId) {
        return geoService.getCitiesOfCountry(countryId);
    }

    @PutMapping("/load")
    public String uploadData() {
        return geoService.uploadData();
    }

}
