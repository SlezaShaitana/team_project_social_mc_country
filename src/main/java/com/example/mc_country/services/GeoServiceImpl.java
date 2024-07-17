package com.example.mc_country.services;

import com.example.mc_country.dto.CityDto;
import com.example.mc_country.dto.CountryDto;
import com.example.mc_country.entity.Country;
import com.example.mc_country.repositories.CityRepository;
import com.example.mc_country.repositories.CountryRepository;
import com.example.mc_country.utils.MappingCity;
import com.example.mc_country.utils.MappingCountry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoServiceImpl implements GeoService{

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    @Override
    public List<CountryDto> getCountries() {
        log.debug("The request was executed: Getting a list of countries");
        return countryRepository.findAll()
                .stream()
                .map(MappingCountry::mapToDto)
                .toList();
    }

    @Override
    public List<CityDto> getCities(String countryId) {
        Country country = countryRepository.findById(countryId).orElse(null);
        log.debug("The request was executed: Getting a list of cities in the country");
        if (country == null){
            return List.of();
        }
        return cityRepository.findByCountry(country)
                .stream()
                .map(MappingCity::mapToDto)
                .toList();
    }

    @Override
    public String uploadData() {
        return "";
    }
}
