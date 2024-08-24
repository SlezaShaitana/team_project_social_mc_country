package com.example.mc_country.response.services;

import com.example.mc_country.dto.redis.IndexCountry;
import com.example.mc_country.dto.response.CountryDto;
import com.example.mc_country.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import ru.yaal.project.hhapi.dictionary.entry.entries.area.Area;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Проверка GeoService:")
public class GeoServiceTest extends AbstractRedisTest {

    @Test
    @DisplayName("Запуск контейнера")
    void givenRedisContainerConfiguredWithDynamicProperties_whenCheckingRunningStatus_thenStatusIsRunning() {
        assertTrue(REDIS_CONTAINER.isRunning());
    }

    @Test
    @DisplayName("GET-запрос: .../country")
    public void whenGetCountries_thenReturnAllCountries() throws Exception{
        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());

        mockMvc.perform(get("/api/v1/geo/country"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(2, Objects.requireNonNull(redisTemplate.keys("*")).size());
    }

    @Test
    @DisplayName("GET-запрос: .../country/{countryId}/city")
    public void whenGetCities_thenReturnAllCities() throws Exception{
        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());

        String countryId = "2a7071a8-f9f0-4423-a6f5-f723d9a71013";
        IndexCountry index = new IndexCountry("1");
        String redisKey = "Index_" + countryId;
        redisTemplate.opsForList().leftPush(redisKey,index);


        mockMvc.perform(get("/api/v1/geo/country/"+ countryId + "/city"))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get("/api/v1/geo/country/"+ countryId + "/city"))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(redisTemplate.keys("*"));

        Mockito.verify(geoClient, Mockito.times(1)).getCountryByIdCountryOfHhApi("1");
    }

    @Test
    @DisplayName("PUT-запрос: .../load")
    public void whenLoad_thenReturnMassage() throws Exception{
        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());

        mockMvc.perform(put("/api/v1/geo/load"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(3, Objects.requireNonNull(redisTemplate.keys("*")).size());
    }

    @ParameterizedTest
    @DisplayName("Получение данных после PUT-запроса")
    @ValueSource(strings = {"получение стран","получение городов"})
    public void whenGetResult_after_put_request(String request) throws Exception{
        mockMvc.perform(put("/api/v1/geo/load"))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(!Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());

        int countKeys = redisTemplate.keys("*").size();
        List<CountryDto> countries = geoService.getAllCountries();
        CountryDto country = countries.get(0);

        switch (request){
            case "получение стран" -> {
                Mockito.verify(geoClient, Mockito.times(1)).getCountries();
                assertTrue(redisTemplate.keys("*").size() == countKeys - 1);
            }
            case "получение городов" -> {
                String countryId = country.getId().toString();
                geoService.getCitiesOfCountry(UUID.fromString(countryId));
                Mockito.verify(geoClient, Mockito.times(1)).getCountryByIdCountryOfHhApi("1");
                assertTrue(redisTemplate.keys("*").size() == countKeys - 2);
            }
        }
    }

    @ParameterizedTest
    @DisplayName("Выброс ошибок класса ResourceNotFoundException")
    @ValueSource(strings = {"получение стран","получение городов", "полная загрузка"})
    public void whenGetResult_thenReturnError(String request) throws Exception{
        switch (request){
            case "получение стран" -> {
                doThrow(Exception.class).when(geoClient);
                assertThrows(ResourceNotFoundException.class, () -> geoService.getAllCountries());
            }
            case "получение городов" -> {
//                assertThrows(ResourceNotFoundException.class, () -> geoService.getCitiesOfCountry("jllk"));
                assertThrows(ResourceNotFoundException.class, () -> geoService.getCitiesOfCountry(UUID
                        .randomUUID()));
            }
            case "полная загрузка" ->{
                doThrow(Exception.class).when(geoClient);
                assertThrows(ResourceNotFoundException.class, () -> geoService.uploadData());
            }
        }
    }


        @ParameterizedTest
        @DisplayName("Выброс ошибок с классов-утилит")
        @ValueSource(strings = {"FullLoader","GetterCities", "GetterCountries"})
        public void whenGetResultUtils_thenReturnError(String request) throws Exception{
            List<Area> areas = new ArrayList<>();
            areas.add(new Area());
            switch (request){
                case "FullLoader" -> {
                    Mockito.when(geoClient.getCountries()).thenReturn(areas);
                    assertThrows(ResourceNotFoundException.class, () -> geoService.getAllCountries());
                }
                case "GetterCities" ->{
                    String countryId = "2a7071a8-f9f0-4423-a6f5-f723d9a71013";
                    IndexCountry index = new IndexCountry("1");
                    String redisKey = "Index_" + countryId;
                    redisTemplate.opsForList().leftPush(redisKey,index);
                    doThrow(Exception.class).when(geoClient);
                    assertThrows(ResourceNotFoundException.class, () -> geoService.getCitiesOfCountry(UUID.fromString(countryId)));
                }case "GetterCountries" ->{
                    Mockito.when(geoClient.getCountries()).thenReturn(areas);
                    assertThrows(ResourceNotFoundException.class, () -> geoService.uploadData());
                }
        }
    }


}
