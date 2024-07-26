package com.example.mc_country.utils;

import com.example.mc_country.data_hhApi.CountryData;
import com.example.mc_country.dto.CityDto;
import com.example.mc_country.dto.CountryDto;
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
    private Map<UUID, List<CityDto>> citiesOfCountry;
    @Getter
    private static String error;

    public FullLoader(List<Area> areas, GeoClient geoClient, Map<UUID, List<CityDto>> citiesOfCountry) {
        this.areas = areas;
        this.geoClient = geoClient;
        this.citiesOfCountry = citiesOfCountry;
    }

    @Override
    protected List<CountryDto> compute() {
        if (error != null){
            return List.of();
        }
        List<CountryDto> countries = new ArrayList<>();
        UUID countryId = UUID.randomUUID();

        String indexFromHhApi = "";
        try {
            indexFromHhApi = areas.get(0).getSearchParameters().getParameterMap().
                    entrySet().iterator().next().getValue().get(0);
        } catch (SearchException e) {
            log.warn("Не получен индекс страны {}. Названия городов этой страны получить невозможно! " +
                    "Error: {}", areas.get(0).getName(), e.getMessage());
            e.printStackTrace();
            error = e.getMessage();
        }

        try {
            CountryData countryData = geoClient.getCountryByIdCountryOfHhApi(indexFromHhApi);

            List<String> titleCitiyList = new ArrayList<>();
            List<CityDto> cities = new ArrayList<>();

            countryData.getAreas().forEach(element -> getCitiesOfCountryData(element, countryId,
                    titleCitiyList, cities));

            CountryDto countryDto =
                    new CountryDto(countryId, true, areas.get(0).getName(), titleCitiyList);
            countries.add(countryDto);
            citiesOfCountry.put(countryId, cities);
        }catch (Exception e){
            e.printStackTrace();
            log.warn("Названия городов страны {} получить невозможно! " +
                    "Error: {}", areas.get(0).getName(), e.getMessage());
            error = e.getMessage();
        }

        if (areas.size() > 1){
            List<FullLoader> taskList = new ArrayList<>();
            for (int i = 1; i < areas.size(); i++){
                FullLoader task = new FullLoader(List.of(areas.get(i)), geoClient, citiesOfCountry);
                task.fork();
                taskList.add(task);
            }
            for (int i = 0; i < taskList.size(); i++) {
                countries.addAll(taskList.get(i).join());
            }
        }
        return countries;
    }

    private void getCitiesOfCountryData(CountryData countryData, UUID countryId,
                                        List<String> titleCitiesList, List<CityDto> cities){

        if (countryData.getParentId() != null && countryData.getAreas().isEmpty()) {
            String cityTitle = countryData.getName();
            titleCitiesList.add(cityTitle);

            CityDto city = new CityDto(UUID.randomUUID(), true, cityTitle, countryId);
            cities.add(city);
        }
        if (countryData.getParentId() != null && !countryData.getAreas().isEmpty()){
            countryData.getAreas().forEach(element ->
                    getCitiesOfCountryData(element, countryId, titleCitiesList, cities));
        }
    }

    public static void cleanError(){
        error = null;
    }
}