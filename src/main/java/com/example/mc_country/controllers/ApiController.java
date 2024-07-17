package com.example.mc_country.controllers;

import com.example.mc_country.dto.CityDto;
import com.example.mc_country.dto.CountryDto;
import com.example.mc_country.services.GeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/geo")
public class ApiController {

    private final GeoService geoService;

    @GetMapping("/country")
    public ResponseEntity<List<CountryDto>> countries() {
        return ResponseEntity.ok(geoService.getCountries());
    }

    @GetMapping("/country/{countryId}/city")
    public ResponseEntity<List<CityDto>> cities(@PathVariable String countryId) {
        return ResponseEntity.ok(geoService.getCities(countryId));
    }

    @PutMapping("/load")
    public ResponseEntity<String> uploadData() {
        return ResponseEntity.ok(geoService.uploadData());
    }

}
