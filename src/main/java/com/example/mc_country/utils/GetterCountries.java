package com.example.mc_country.utils;

import com.example.mc_country.dto.HhApi.CountryDataFromHhApi;
import com.example.mc_country.dto.redis.IndexCountry;
import com.example.mc_country.dto.response.CityDto;
import com.example.mc_country.dto.response.CountryDto;
import com.example.mc_country.exception.ResourceNotFoundException;
import com.example.mc_country.feign.GeoClient;
import com.example.mc_country.services.GeoServiceImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.yaal.project.hhapi.dictionary.entry.entries.area.Area;
import ru.yaal.project.hhapi.search.SearchException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.RecursiveTask;

@Slf4j
public class GetterCountries extends RecursiveTask<List<CountryDto>> {

    private final List<Area> areas;
    private final GeoClient geoClient;
    private final Map<String, IndexCountry> indexes;
    @Getter
    private static String error = "";

    public GetterCountries(List<Area> areas, GeoClient geoClient, Map<String, IndexCountry> indexes) {
        this.areas = areas;
        this.geoClient = geoClient;
        this.indexes = indexes;
    }

    @Override
    protected List<CountryDto> compute() {
        if (!error.isEmpty()) return List.of();

        List<CountryDto> countries = new ArrayList<>();
        String countryId = String.valueOf(UUID.randomUUID());
        log.info(countryId);
        String indexFromHhApi = "";
        try {
            indexFromHhApi = areas.get(0).getSearchParameters().getParameterMap().
                    entrySet().iterator().next().getValue().get(0);
            indexes.put(countryId,new IndexCountry(indexFromHhApi));
        } catch (SearchException e) {
            e.printStackTrace();
            error = "Не получен индекс страны " + areas.get(0).getName()
                    + " Названия городов этой страны получить невозможно! Error: " + e.getMessage();
            return List.of();
        }

        try {
            countries.add(createCountryDto(indexFromHhApi, countryId));
        }catch (Exception e){
            e.printStackTrace();
            error = "Названия городов страны " + areas.get(0).getName()
                    + " получить невозможно! Error: " + e.getMessage();
            return List.of();
        }

        if (areas.size() > 1){
           workTasks(countries);
        }
        return countries;
    }


    private void workTasks(List<CountryDto> countries){
        List<GetterCountries> taskList = new ArrayList<>();
        for (int i = 1; i < areas.size(); i++){
            GetterCountries task = new GetterCountries(List.of(areas.get(i)), geoClient, indexes);
            task.fork();
            taskList.add(task);
        }
        for (int i = 0; i < taskList.size(); i++) {
            countries.addAll(taskList.get(i).join());
        }
    }

    private CountryDto createCountryDto (String indexFromHhApi, String countryId){
        CountryDataFromHhApi countryDataFromHhApi = geoClient.getCountryByIdCountryOfHhApi(indexFromHhApi);

        List<CityDto> cities = new ArrayList<>();
        countryDataFromHhApi.getAreas().forEach(element -> getCitiesOfCountryData(element, cities, countryId));

        return new CountryDto(countryId, true, areas.get(0).getName(), cities);
    }


    private void getCitiesOfCountryData(CountryDataFromHhApi countryDataFromHhApi, List<CityDto> cities,
                                        String countryId){
        if (countryDataFromHhApi.getParentId() != null && countryDataFromHhApi.getAreas().isEmpty()) {
            cities.add(new CityDto(
                    String.valueOf(UUID.randomUUID()),
                    true,
                    countryDataFromHhApi.getName(),
                    countryId
            ));
        }
        if (countryDataFromHhApi.getParentId() != null && !countryDataFromHhApi.getAreas().isEmpty()){
            countryDataFromHhApi.getAreas().forEach(element ->
                    getCitiesOfCountryData(element, cities, countryId));
        }
    }

    public static void cleanError(){
        error = "";
    }
}
