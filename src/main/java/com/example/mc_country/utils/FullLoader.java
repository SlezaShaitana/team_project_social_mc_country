package com.example.mc_country.utils;

import com.example.mc_country.dto.HhApi.CountryDataFromHhApi;
import com.example.mc_country.dto.response.CityDto;
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
public class FullLoader extends RecursiveTask<List<CountryDto>> {
    private final List<Area> areas;
    private final GeoClient geoClient;
    private Map<String, List<CityDto>> cities;
    @Getter
    private static String error = "";

    public FullLoader(List<Area> areas, Map<String, List<CityDto>> cities, GeoClient geoClient) {
        this.areas = areas;
        this.geoClient = geoClient;
        this.cities = cities;
    }

    @Override
    protected List<CountryDto> compute() {
        if (!error.isEmpty()) return List.of();

        String countryId = UUID.randomUUID().toString();
        String indexFromHhApi = "";
        try {
            indexFromHhApi = areas.get(0).getSearchParameters().getParameterMap().
                    entrySet().iterator().next().getValue().get(0);
        } catch (SearchException e) {
            e.printStackTrace();
            error = "Не получен индекс страны " + areas.get(0).getName() +
                    ". Названия городов этой страны получить невозможно! Error: " +  e.getMessage();
            return List.of();
        }
        List<CountryDto> countries = new ArrayList<>();
        try {
             saveResults(countryId, indexFromHhApi, countries);
        }catch (Exception e){
            e.printStackTrace();
            error = "Названия городов страны " + areas.get(0).getName() +
                    " получить невозможно! Error: " + e.getMessage();
            return List.of();
        }

        if (areas.size() > 1){
            List<FullLoader> taskList = createTasks();
            for (int i = 0; i < taskList.size(); i++) {
                countries.addAll(taskList.get(i).join());
            }
        }
        return countries;
    }

    private void saveResults(String countryId, String indexFromHhApi, List<CountryDto> countries){
        CountryDataFromHhApi countryDataFromHhApi = geoClient.getCountryByIdCountryOfHhApi(indexFromHhApi);

        List<String> titleCitiyList = new ArrayList<>();
        List<CityDto> citiesOfCountry = new ArrayList<>();

        countryDataFromHhApi.getAreas().forEach(element -> getCitiesOfCountryData(element, countryId,
                titleCitiyList, citiesOfCountry));

        CountryDto countryDto =
                new CountryDto(countryId, true, areas.get(0).getName(), titleCitiyList);
        countries.add(countryDto);
        cities.put(String.valueOf(countryId), citiesOfCountry);
    }

    private List<FullLoader> createTasks(){
        List<FullLoader> taskList = new ArrayList<>();
        for (int i = 1; i < areas.size(); i++){
            FullLoader task = new FullLoader(List.of(areas.get(i)), cities, geoClient);
            task.fork();
            taskList.add(task);
        }
        return taskList;
    }

    private void getCitiesOfCountryData(CountryDataFromHhApi countryDataFromHhApi, String countryId,
                                        List<String> titleCitiesList, List<CityDto> cities){

        if (countryDataFromHhApi.getParentId() != null && countryDataFromHhApi.getAreas().isEmpty()) {
            String cityTitle = countryDataFromHhApi.getName();
            titleCitiesList.add(cityTitle);

            CityDto city = new CityDto(UUID.randomUUID().toString(), true, cityTitle, countryId);
            cities.add(city);
        }
        if (countryDataFromHhApi.getParentId() != null && !countryDataFromHhApi.getAreas().isEmpty()){
            countryDataFromHhApi.getAreas().forEach(element ->
                    getCitiesOfCountryData(element, countryId, titleCitiesList, cities));
        }
    }

    public static void cleanError(){
        error = "";
    }
}