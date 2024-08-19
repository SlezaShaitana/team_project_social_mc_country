package com.example.mc_country.test_utils;

import com.example.mc_country.dto.HhApi.CountryDataFromHhApi;
import ru.yaal.project.hhapi.dictionary.entry.entries.area.Area;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class GetDataFromHhApi {

    public List<Area> createCountries() throws MalformedURLException {
        Area countryOne = new Area();
        countryOne.setId("1");
        countryOne.setName("Country 1");
        countryOne.setUrl(new URL("https://api.hh.ru/areas/1"));
        Area countryTwo = new Area();
        countryTwo.setId("5");
        countryTwo.setName("Country 2");
        countryTwo.setUrl(new URL("https://api.hh.ru/areas/5"));

        List<Area> countries = new ArrayList<>();
        countries.add(countryOne);
        countries.add(countryTwo);
        return countries;
    }


    public CountryDataFromHhApi createDataOfCountry(){
        CountryDataFromHhApi dataOne = new CountryDataFromHhApi(
                3L,
                2L,
                "City 1",
                List.of()
        );

        CountryDataFromHhApi dataTwo = new CountryDataFromHhApi(
                4L,
                2L,
                "City 2",
                List.of()
        );
        CountryDataFromHhApi dataTree = new CountryDataFromHhApi(
                2L,
                1L,
                "Republic 1",
                List.of(dataOne, dataTwo)
        );
        CountryDataFromHhApi data = new CountryDataFromHhApi(
                1L,
                null,
                "Country 1",
                List.of(dataTree));

        return data;
    }
}
