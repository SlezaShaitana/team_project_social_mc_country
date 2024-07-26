package com.example.mc_country.utils;

import com.example.mc_country.data_hhApi.CountryData;
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
public class GetterCountries extends RecursiveTask<List<CountryDto>> {

    private Map<UUID, String> indexesCountryFromHhApi;
    private final List<Area> areas;
    private final GeoClient geoClient;
    @Getter
    private static String error;

    public GetterCountries(List<Area> areas, GeoClient geoClient,
                           Map<UUID, String> indexesCountryFromHhApi) {
        this.areas = areas;
        this.geoClient = geoClient;
        this.indexesCountryFromHhApi = indexesCountryFromHhApi;
    }

    @Override
    protected List<CountryDto> compute() {
        List<CountryDto> countries = new ArrayList<>();
            UUID countryId = UUID.randomUUID();

            String indexFromHhApi = "";
            try {
                indexFromHhApi = areas.get(0).getSearchParameters().getParameterMap().
                        entrySet().iterator().next().getValue().get(0);
                indexesCountryFromHhApi.put(countryId, indexFromHhApi);
            } catch (SearchException e) {
                log.warn("Не получен индекс страны {}. Названия городов этой страны получить невозможно! " +
                        "Error: {}", areas.get(0).getName(), e.getMessage());
                e.printStackTrace();
                error = e.getMessage();
            }

            try {
                CountryData countryData = geoClient.getCountryByIdCountryOfHhApi(indexFromHhApi);

                List<String> cities = new ArrayList<>();
                countryData.getAreas().forEach(element -> getCitiesOfCountryData(element, cities));

                CountryDto countryDto =
                        new CountryDto(countryId, true, areas.get(0).getName(), cities);
                countries.add(countryDto);
            }catch (Exception e){
                e.printStackTrace();
                log.warn("Названия городов страны {} получить невозможно! " +
                        "Error: {}", areas.get(0).getName(), e.getMessage());
                error = e.getMessage();
            }

            if (areas.size() > 1){
                List<GetterCountries> taskList = new ArrayList<>();
                for (int i = 1; i < areas.size(); i++){
                    GetterCountries task = new GetterCountries(List.of(areas.get(i)), geoClient,
                            indexesCountryFromHhApi);
                    task.fork();
                    taskList.add(task);
                }
                for (int i = 0; i < taskList.size(); i++) {
                    countries.addAll(taskList.get(i).join());
                }
            }
        return countries;
    }

    private void getCitiesOfCountryData(CountryData countryData, List<String> cities){
        if (countryData.getParentId() != null && countryData.getAreas().isEmpty()) {
            cities.add(countryData.getName());
        }
        if (countryData.getParentId() != null && !countryData.getAreas().isEmpty()){
            countryData.getAreas().forEach(element ->
                    getCitiesOfCountryData(element, cities));
        }
    }

    public static void cleanError(){
        error = null;
    }
}
