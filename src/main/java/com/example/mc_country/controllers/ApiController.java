package com.example.mc_country.controllers;

import com.example.mc_country.dto.response.CityDto;
import com.example.mc_country.dto.response.CityDtoList;
import com.example.mc_country.dto.response.CountryDto;
import com.example.mc_country.services.GeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public CityDtoList cities(@PathVariable UUID countryId) {
        List<CityDto> cities = geoService.getCitiesOfCountry(countryId);
        CityDtoList list = new CityDtoList();
        list.setCities(cities);
        return list;
    }

    @PutMapping("/load")
    public String uploadData() {
        return geoService.uploadData();
    }

}
