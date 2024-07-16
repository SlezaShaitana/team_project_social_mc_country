package com.example.mc_country.controllers;

import com.example.mc_country.model.City;
import com.example.mc_country.model.Country;
import com.example.mc_country.services.GeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/geo")
public class ApiController {
    private final GeoService geoService;

    @GetMapping("/country")
    public ResponseEntity<List<Country>> countries() {
        return ResponseEntity.ok(geoService.getCountries());
    }

    @GetMapping("/country/{countryId}/city")
    public ResponseEntity<List<City>> cities(@PathVariable String countryId) {
        return ResponseEntity.ok(geoService.getCities(countryId));
    }

    @PutMapping("/load")
    public ResponseEntity<String> uploadData() {
        return ResponseEntity.ok(geoService.uploadData());
    }

}
