package com.example.mc_country.services;

import com.example.mc_country.configuration.properties.AppCacheProperties;
import com.example.mc_country.dto.CityDto;
import com.example.mc_country.dto.CountryDto;
import com.example.mc_country.feign.GeoClient;
import com.example.mc_country.utils.FullLoader;
import com.example.mc_country.utils.GetterCities;
import com.example.mc_country.utils.GetterCountries;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.yaal.project.hhapi.dictionary.entry.entries.area.Area;


import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ForkJoinPool;


@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "redisCacheManager")
@Slf4j
public class GeoServiceImpl implements GeoService{

    private final GeoClient geoClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private Map<UUID, String> indexesCountryFromHhApi;

    @Resource(name="redisTemplate")
    private ListOperations<String, List<CountryDto>> listOpsCountry;
    @Resource(name="redisTemplate")
    private ListOperations<String, List<CityDto>> listOpsCity;

    @Override
    @Cacheable(cacheNames = AppCacheProperties.CacheNames.ALL_COUNTRIES)
    public List<CountryDto> getAllCountries() {
        log.info("Получен запрос на получение списка стран");
        GetterCountries.cleanError();

        List<CountryDto> countryDtoList = null; // загрузка данных с redis
        if (countryDtoList != null){
            return countryDtoList;
        }

        List<Area> areas;
        indexesCountryFromHhApi = new HashMap<>();
        try {
            areas = geoClient.getCountries();
        }catch (Exception e){
            e.printStackTrace();
            log.warn("Данные стран получить невозможно! Error: {}", e.getMessage());
            return List.of();
        }
        List<CountryDto> countries = new  ForkJoinPool().invoke(new GetterCountries(areas, geoClient, indexesCountryFromHhApi));

        if (GetterCountries.getError() != null){
            GetterCountries.cleanError();
            return List.of();
        }
        return countries;
    }

    @Override
    @Cacheable(cacheNames = AppCacheProperties.CacheNames.ALL_CITIES_OF_COUNTRY, key = "#countryId")
    public List<CityDto> getCitiesOfCountry(UUID countryId) {
        log.info("Получен запрос на получение списка городов страны c id: {}", countryId);

        List<CityDto> cities = null; // загрузка данных с redis
        if (cities != null){
            return cities;
        }

        if (indexesCountryFromHhApi == null){
            log.warn("Не выполнен запрос на получение списка стран! Данные отсутствуют!");
            return List.of();
        }
        String indexFromHhApi = indexesCountryFromHhApi.get(countryId);
        if (indexFromHhApi == null){
            log.warn("Страна с введенным параметром id: {} не найдена!", countryId);
            return List.of();
        }else {
            return GetterCities.getCities(countryId, indexFromHhApi, geoClient);
        }
    }

    @Override
    public String uploadData() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
        indexesCountryFromHhApi = null;

        log.info("** Старт загрузки данных ** {}", LocalTime.now().truncatedTo(ChronoUnit.SECONDS));

        List<Area> areas = geoClient.getCountries();

        Map<UUID, List<CityDto>> citiesOfCountryMap = new HashMap<>();
        List<CountryDto> countries = new ForkJoinPool().invoke(new FullLoader(areas, geoClient, citiesOfCountryMap));

        String errorResult = "Данные не загружены!";
        if (countries.isEmpty()){
            return errorResult;
        }

        if (FullLoader.getError() == null){
            listOpsCountry.leftPush("Countries" , countries);
            for (Map.Entry entry : citiesOfCountryMap.entrySet()){
                List<CityDto> cities = (List<CityDto>) entry.getValue();
                listOpsCity.leftPush(entry.getKey().toString(), cities);
            }
            log.info("** Завершение загрузки данных ** {}", LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
            return "";
        }else {
            log.warn("Данные не загружены или загружены не в полном объеме!");
            FullLoader.cleanError();
            return errorResult;
        }
    }
}
