package com.example.mc_country.response.controllers;

import com.example.mc_country.test_utils.GetCountriesAndCities;
import com.example.mc_country.exception.ResourceNotFoundException;
import com.example.mc_country.test_utils.StringTestUtils;
import com.example.mc_country.services.GeoService;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false, webClientEnabled = false)
@DisplayName("Тесты на результаты запросов:")
public class ApiControllerTest extends GetCountriesAndCities {

    @MockBean
    private GeoService geoService;

    @Autowired
    protected MockMvc mockMvc;


    @Test
    @DisplayName("GET-запрос: .../country")
    public void whenGetCountries_thenReturnAllCountries() throws Exception{
        Mockito.when(geoService.getAllCountries()).thenReturn(getCountries());

        String actualResponse = mockMvc.perform(get("/api/v1/geo/country"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = StringTestUtils.readStringFromResource(
                "response/get_countries_response.json");

        Mockito.verify(geoService, Mockito.times(1)).getAllCountries();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);

    }

    @ParameterizedTest
    @DisplayName("GET-запрос: .../country/{countryId}/city")
    @ValueSource(strings = {countryOneId, errorId})
    public void whenGetCities_thenReturnAllCitiesOfCountry(String countryId) throws Exception{
        switch (countryId){
            case countryOneId -> {
                Mockito.when(geoService.getCitiesOfCountry(UUID.fromString(countryOneId))).thenReturn(getCities());

                String actualResponse = mockMvc.perform(
                        get("/api/v1/geo/country/" + countryOneId + "/city"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                String expectedResponse = StringTestUtils.readStringFromResource(
                        "response/get_cities_of_country.json");

                Mockito.verify(geoService, Mockito.times(1)).getCitiesOfCountry(UUID.fromString(countryOneId));

                JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
            }
            case errorId -> {
                Mockito.when(geoService.getCitiesOfCountry(UUID.fromString(errorId)))
                        .thenThrow(new ResourceNotFoundException("Введенное значение " +
                                errorId + " не соответствует типу UUID!"));
                var response = mockMvc.perform(get("/api/v1/geo/country/" + errorId + "/city"))
                        .andExpect(status().isNotFound())
                        .andReturn()
                        .getResponse();
                response.setCharacterEncoding("UTF-8");

                String actualResponse = response.getContentAsString();
                String expectedResponse = StringTestUtils.readStringFromResource(
                        "response/get_cities_of_country_by_exception_id.json");

                Mockito.verify(geoService, Mockito.times(1)).getCitiesOfCountry(UUID.fromString(errorId));

                JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
            }
        }
    }

    @Test
    @DisplayName("PUT-запрос: .../load")
    public void whenLoad_thenReturnMessage() throws Exception{
        Mockito.when(geoService.uploadData()).thenReturn("");

        String actualResponse = mockMvc.perform(put("/api/v1/geo/load"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = "";

        Mockito.verify(geoService, Mockito.times(1)).uploadData();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);

    }
}
