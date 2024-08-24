package com.example.mc_country.utils;

import com.example.mc_country.dto.HhApi.CountryDataFromHhApi;
import com.example.mc_country.dto.redis.IndexCountry;
import com.example.mc_country.dto.response.CountryDto;
import com.example.mc_country.exception.ResourceNotFoundException;
import com.example.mc_country.feign.GeoClient;
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
    private Map<String, IndexCountry> indexes;
    @Getter
    private static String error = "";

    public GetterCountries(List<Area> areas, Map<String, IndexCountry> indexes, GeoClient geoClient) {
        this.areas = areas;
        this.indexes = indexes;
        this.geoClient = geoClient;
    }

    @Override
    protected List<CountryDto> compute() {
        if (!error.isEmpty()) return List.of();

        List<CountryDto> countries = new ArrayList<>();
        UUID countryId = UUID.randomUUID();
        String indexFromHhApi = "";
        try {
            indexFromHhApi = areas.get(0).getSearchParameters().getParameterMap().
                    entrySet().iterator().next().getValue().get(0);
           indexes.put(String.valueOf(countryId), new IndexCountry(indexFromHhApi));
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
            GetterCountries task = new GetterCountries(List.of(areas.get(i)), indexes, geoClient);
            task.fork();
            taskList.add(task);
        }
        for (int i = 0; i < taskList.size(); i++) {
            countries.addAll(taskList.get(i).join());
        }
    }

    private CountryDto createCountryDto (String indexFromHhApi, UUID countryId){
        CountryDataFromHhApi countryDataFromHhApi = geoClient.getCountryByIdCountryOfHhApi(indexFromHhApi);

        List<String> cities = new ArrayList<>();
        countryDataFromHhApi.getAreas().forEach(element -> getCitiesOfCountryData(element, cities));

        return new CountryDto(null, true, areas.get(0).getName(), cities);
    }


    private void getCitiesOfCountryData(CountryDataFromHhApi countryDataFromHhApi, List<String> cities){
        if (countryDataFromHhApi.getParentId() != null && countryDataFromHhApi.getAreas().isEmpty()) {
            cities.add(countryDataFromHhApi.getName());
        }
        if (countryDataFromHhApi.getParentId() != null && !countryDataFromHhApi.getAreas().isEmpty()){
            countryDataFromHhApi.getAreas().forEach(element ->
                    getCitiesOfCountryData(element, cities));
        }
    }

    public static void cleanError(){
        error = "";
    }
}
