package com.example.mc_country.services;

import com.example.mc_country.configuration.properties.AppCacheProperties;
import com.example.mc_country.dto.redis.IndexCountry;
import com.example.mc_country.dto.response.CityDto;
import com.example.mc_country.dto.response.CountryDto;
import com.example.mc_country.dto.redis.RedisKeyName;
import com.example.mc_country.exception.ResourceNotFoundException;
import com.example.mc_country.feign.GeoClient;
import com.example.mc_country.utils.FullLoader;
import com.example.mc_country.utils.GetterCities;
import com.example.mc_country.utils.GetterCountries;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.yaal.project.hhapi.dictionary.entry.entries.area.Area;


import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "redisCacheManager")
@Slf4j
public class GeoServiceImpl implements GeoService{

    private final GeoClient geoClient;
    private final RedisTemplate redisTemplate;
    private final String prefixKeyName = "Index_";


    @Override
    public List<CountryDto> getAllCountries() {
        String redisKey = String.valueOf(RedisKeyName.Countries_List);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))){
            List<CountryDto> countries = (List<CountryDto>) redisTemplate.boundListOps(redisKey).leftPop();
            log.info("Выгрузка стран из Redis завершилась успешно");
            return countries;
        }

        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
        List<Area> areas;
        try {
            areas = geoClient.getCountries();
        }catch (Exception e){
            e.printStackTrace();
            throw new ResourceNotFoundException("Данные стран c HhApi получить невозможно!" +
                    " Error: " + e.getMessage());
        }

        Map<String, IndexCountry> indexes = new HashMap<>();
        List<CountryDto> countries = new  ForkJoinPool().invoke(
                new GetterCountries(areas, indexes, geoClient));

        if (!GetterCountries.getError().isEmpty()){
            String error = GetterCountries.getError();
            GetterCountries.cleanError();
            throw new ResourceNotFoundException(error);
        }

        saveIndexInRedis(indexes);
        log.info("Запрос на получение списка стран выполнен");
        return countries;
    }

    @Override
    @Cacheable(cacheNames = AppCacheProperties.CacheNames.CITIES_OF_COUNTRY, key = "#countryId")
    public List<CityDto> getCitiesOfCountry(String countryId) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(countryId))){
            List<CityDto> cities = (List<CityDto>) redisTemplate.boundListOps(countryId.toString()).leftPop();
            log.info("Выгрузка городов страны с id: {} из Redis завершилась успешно", countryId);
            return cities;
        }else if (Boolean.TRUE.equals(redisTemplate.hasKey(prefixKeyName + countryId))){
            IndexCountry index = (IndexCountry) redisTemplate.boundListOps(prefixKeyName + countryId).leftPop();
            log.info("Индекс страны с id: {} получен", countryId);

            List<CityDto> cities = GetterCities.getCities(countryId, index.getIndex(), geoClient);

            if (!GetterCities.getError().isEmpty()){
                String error = GetterCities.getError();
                GetterCities.cleanError();
                throw new ResourceNotFoundException(error);
            }
            return cities;
        }else {
            throw new ResourceNotFoundException("Данные отсутствуют! Выполните GET-запрос " +
                    "на получение списка стран или сделайте полную выгрузку PUT-запросом");
        }
    }

    @Override
    public String uploadData() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
        log.info("** Старт загрузки данных ** {}", LocalTime.now().truncatedTo(ChronoUnit.SECONDS));

        List<Area> areas;
        try {
            areas = geoClient.getCountries();
        }catch (Exception e){
            e.printStackTrace();
            throw new ResourceNotFoundException("Данные стран c HhApi получить невозможно!" +
                    " Error: " + e.getMessage());
        }
        Map<String, List<CityDto>> cities = new HashMap<>();
        List<CountryDto> countries = new ForkJoinPool().invoke(new FullLoader(areas, cities,geoClient));

        if (!FullLoader.getError().isEmpty()){
            String error = FullLoader.getError();
            FullLoader.cleanError();
            throw new ResourceNotFoundException(error);
        }

        saveListInRedis(redisTemplate, String.valueOf(RedisKeyName.Countries_List), countries);
        for (Map.Entry country : cities.entrySet()){
            String key = country.getKey().toString();
            List<CityDto> value = (List<CityDto>) country.getValue();
            saveListInRedis(redisTemplate, key, value);
        }
        log.info("** Завершение загрузки данных ** {}", LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        return "";
    }


    private void saveListInRedis(RedisTemplate redisTemplate, String keyName, List data){
        String redisKey = String.valueOf(keyName);
        redisTemplate.opsForList().leftPush(redisKey,data);
        redisTemplate.expire(redisKey, 1, TimeUnit.DAYS);
    }

    private void saveIndexInRedis(Map<String, IndexCountry> indexes){
        for (Map.Entry index : indexes.entrySet()){
            String key = prefixKeyName + index.getKey();
            IndexCountry value = (IndexCountry) index.getValue();
            redisTemplate.opsForList().leftPush(key,value);
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        }
    }
}
