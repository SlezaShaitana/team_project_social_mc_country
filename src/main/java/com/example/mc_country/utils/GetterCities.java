package com.example.mc_country.utils;

import com.example.mc_country.data_hhApi.CountryData;
import com.example.mc_country.dto.CityDto;
import com.example.mc_country.feign.GeoClient;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class GetterCities {


    public static List<CityDto> getCities(UUID countryId, String indexFromHhApi, GeoClient geoClient){
        List<CityDto> cities = new ArrayList<>();
        try {
            CountryData countryData = geoClient.getCountryByIdCountryOfHhApi(indexFromHhApi);
            countryData.getAreas().forEach(element ->{
                getCitiesOfCountryData(cities, element, countryId);
            });
            if (cities.isEmpty()){
                log.warn("Список городов страны с id: {} пуст!", countryId);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.warn("Список городов страны с id: {} получить невозможно! " +
                    "Error: {}", countryId, e.getMessage());
        }

        return cities;
    }

    private static void getCitiesOfCountryData(List<CityDto> cities, CountryData countryData, UUID countryId){
        if (countryData.getParentId() != null && countryData.getAreas().isEmpty()){
            CityDto cityDto =
                    new CityDto(UUID.randomUUID(), true, countryData.getName(),countryId);

            cities.add(cityDto);
        }

        if (countryData.getParentId() != null && !countryData.getAreas().isEmpty()){
            countryData.getAreas().forEach(element ->
                    getCitiesOfCountryData(cities, element, countryId));
        }
    }
}
