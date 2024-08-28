package com.example.mc_country.utils;

import com.example.mc_country.dto.HhApi.CountryDataFromHhApi;
import com.example.mc_country.dto.response.CityDto;
import com.example.mc_country.exception.ResourceNotFoundException;
import com.example.mc_country.feign.GeoClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class GetterCities {
    @Getter
    private static String error = "";

    public static List<CityDto> getCities(String countryId, String indexFromHhApi, GeoClient geoClient){
        List<CityDto> cities = new ArrayList<>();
        try {
            CountryDataFromHhApi countryDataFromHhApi = geoClient.getCountryByIdCountryOfHhApi(indexFromHhApi);
            countryDataFromHhApi.getAreas().forEach(element ->{
                getCitiesOfCountryData(cities, element, countryId);
            });
            if (cities.isEmpty()){
                log.warn("Список городов страны с id: {} пуст!", countryId);
            }
        }catch (Exception e){
            e.printStackTrace();
            error = "Список городов страны с id: " + countryId +
                    " получить невозможно! Error: " + e.getMessage();
            return List.of();
        }
        log.info("Запрос на получение списка городов страны с id: {} выполнен", countryId);
        return cities;
    }

    private static void getCitiesOfCountryData(List<CityDto> cities, CountryDataFromHhApi countryDataFromHhApi, String countryId){
        if (countryDataFromHhApi.getParentId() != null && countryDataFromHhApi.getAreas().isEmpty()){
            CityDto cityDto =
                    new CityDto(String.valueOf(UUID.randomUUID()), true, countryDataFromHhApi.getName(),countryId);

            cities.add(cityDto);
        }

        if (countryDataFromHhApi.getParentId() != null && !countryDataFromHhApi.getAreas().isEmpty()){
            countryDataFromHhApi.getAreas().forEach(element ->
                    getCitiesOfCountryData(cities, element, countryId));
        }
    }

    public static void cleanError(){
        error = "";
    }
}
