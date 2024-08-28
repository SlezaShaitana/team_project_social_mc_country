package com.example.mc_country.test_utils;

import com.example.mc_country.dto.response.CityDto;
import com.example.mc_country.dto.response.CountryDto;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class GetCountriesAndCities {
    protected final String countryOneId = "8726fb71-48e2-4140-8da7-513bdfd63395";
    protected final String countryTwoId = "17a74a8d-2143-4244-86a3-00c2aeed8d58";
    protected final String cityOneId = "2a7071a8-f9f0-4423-a6f5-f723d9a71013";
    protected final String cityTwoId = "924d6bad-0f7c-42dd-82fe-7cda182b6dfa";
    protected final String  errorId = "абракадабра";

    private List<CountryDto> countries;
    private List<CityDto> cities;

    @BeforeEach
    public void setUp(){
        countries = new ArrayList<>();
        countries.add(new CountryDto(countryOneId,
                true,
                "Country " + countryOneId,
                List.of(new CityDto(
                        cityOneId,
                        true,
                        "City " + cityOneId,
                        countryOneId
                ),new CityDto(
                        cityTwoId,
                        true,
                        "City " + cityTwoId,
                        countryOneId
                ))
        ));
        countries.add(new CountryDto(
                countryTwoId,
                true,
                "Country " + countryTwoId,
                List.of()
        ));

        cities = new ArrayList<>();
        cities.add(new CityDto(cityOneId, true,
                "City " + cityOneId, countryOneId));
        cities.add(new CityDto(cityTwoId, true,
                "City " + cityTwoId, countryOneId));
    }
}
