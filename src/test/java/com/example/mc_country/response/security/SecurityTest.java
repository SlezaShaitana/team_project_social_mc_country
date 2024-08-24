package com.example.mc_country.response.security;

import com.example.mc_country.feign.JwtClient;
import com.example.mc_country.security.DecodedToken;
import com.example.mc_country.services.GeoService;
import com.example.mc_country.test_utils.StringTestUtils;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc(webClientEnabled = false)
@DisplayName("Тесты на spring security:")
public class SecurityTest extends AbstractTestForSecurity{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeoService geoService;

    @MockBean
    private JwtClient jwtClient;



    private final String urlForGetCountries = "/api/v1/geo/country";
    private final String urlForGetCities = "/api/v1/geo/country/1/city";
    private final String urlForLoad = "/api/v1/geo/load";


    @Test
    @DisplayName("Валидный токен")
    public void whenGetRequest_thenWorkFilter_with_normal_token() throws Exception{
        Mockito.when(jwtClient.validateToken(normalToken)).thenReturn(true);

        Mockito.when(geoService.getAllCountries()).thenReturn(List.of());
        String actualResponseOne = responseByGetRequest(urlForGetCountries, normalToken);
        Mockito.verify(jwtClient, Mockito.times(1)).validateToken(normalToken);
        assertEquals("[]", actualResponseOne);

//        Mockito.when(geoService.getCitiesOfCountry("1")).thenReturn(List.of());
//        String actualResponseTwo = responseByGetRequest(urlForGetCities, normalToken);
//        Mockito.verify(jwtClient, Mockito.times(2)).validateToken(normalToken);
//        assertEquals("[]", actualResponseTwo);

        Mockito.when(geoService.uploadData()).thenReturn("");
        String actualResponseTree = responseByPutRequest(urlForLoad, normalToken);
        Mockito.verify(jwtClient, Mockito.times(3)).validateToken(normalToken);
        assertEquals("", actualResponseTree);
    }

    @Test
    @DisplayName("Фальшивый токен")
    public void whenGetRequest_thenWorkFilter_with_fake_token() throws Exception{
        Mockito.when(jwtClient.validateToken(normalToken)).thenReturn(false);

        Mockito.when(geoService.getAllCountries()).thenReturn(List.of());
        responseByErrorGetRequest(urlForGetCountries, normalToken);
        Mockito.verify(jwtClient, Mockito.times(1)).validateToken(normalToken);


//        Mockito.when(geoService.getCitiesOfCountry("1")).thenReturn(List.of());
//        responseByErrorGetRequest(urlForGetCities, normalToken);
//        Mockito.verify(jwtClient, Mockito.times(2)).validateToken(normalToken);


        Mockito.when(geoService.uploadData()).thenReturn("");
        responseByErrorPutRequest(urlForLoad, normalToken);
        Mockito.verify(jwtClient, Mockito.times(3)).validateToken(normalToken);

    }

    @Test
    @DisplayName("Запрос заголовка недействителен")
    public void whenGetRequest_thenWorkFilter_with_fake_header_request() throws Exception{
        responseByErrorHeader(urlForGetCountries);
        responseByErrorHeader(urlForGetCities);
        responseByErrorHeader(urlForLoad);
        responseByErrorHeaderType(urlForGetCountries);
        responseByErrorHeaderType(urlForGetCities);
        responseByErrorHeader(urlForLoad);
    }


    @Test
    @DisplayName("Декодирование токена: верные значения PAYLOAD")
    public void whenGetStringToken_thenReturnObjectClass() throws Exception{
        String actualResponse = DecodedToken.getDecoded(normalToken).toString();
        String expectedResponse = StringTestUtils.readStringFromResource(
                "response/get_payload_token.json");
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Декодирование токена: неверные значения PAYLOAD")
    public void whenGetStringErrorToken_thenReturnObjectClass() throws Exception{
        String actualResponse = DecodedToken.getDecoded(tokenWithErrorPayload).toString();
        String expectedResponse = "{}";
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }


    private String responseByGetRequest(String url, String token) throws Exception {
        HttpHeaders head = new HttpHeaders();
        head.add("Authorization", "Bearer " + token);
        return mockMvc.perform(get(url).headers(head))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();
    }

    private String responseByPutRequest(String url, String token) throws Exception {
        HttpHeaders head = new HttpHeaders();
        head.add("Authorization", "Bearer " + token);
        return mockMvc.perform(put(url).headers(head))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private void responseByErrorGetRequest(String url, String token) throws Exception {
        HttpHeaders head = new HttpHeaders();
        head.add("Authorization", "Bearer " + token);

        mockMvc.perform(get(url).headers(head))
                .andExpect(status().is(401))
                .andReturn();
    }

    private void responseByErrorPutRequest(String url, String token) throws Exception {
        HttpHeaders head = new HttpHeaders();
        head.add("Authorization", "Bearer " + token);

        mockMvc.perform(put(url).headers(head))
                .andExpect(status().is(401))
                .andReturn();
    }

    private void responseByErrorHeader(String url) throws Exception {
        mockMvc.perform(put(url))
                .andExpect(status().is(401))
                .andReturn();
    }
    private void responseByErrorHeaderType(String url) throws Exception {
        HttpHeaders head = new HttpHeaders();
        head.add("Authorization", "Basic a2poZ2Y6a2poZw==");
        mockMvc.perform(put(url).headers(head))
                .andExpect(status().is(401))
                .andReturn();
    }
}
