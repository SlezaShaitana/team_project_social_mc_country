package com.example.mc_country.feign;

import com.example.mc_country.dto.HhApi.CountryDataFromHhApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.yaal.project.hhapi.dictionary.entry.entries.area.Area;

import java.util.List;

@FeignClient(value = "geo",
        url = "https://api.hh.ru/areas")
public interface GeoClient {

    @RequestMapping(method = RequestMethod.GET, value = "/countries")
    List<Area> getCountries();

    @RequestMapping(method = RequestMethod.GET, value = "/{index}")
    CountryDataFromHhApi getCountryByIdCountryOfHhApi(@PathVariable String index);

}

