package com.example.mc_country.response.services;

import com.example.mc_country.services.GeoService;
import com.example.mc_country.test_utils.GetDataFromHhApi;
import com.example.mc_country.dto.HhApi.CountryDataFromHhApi;
import com.example.mc_country.feign.GeoClient;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;


@SpringBootTest()
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false, webClientEnabled = false)
@Testcontainers
public abstract class AbstractRedisTest extends GetDataFromHhApi {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected RedisTemplate redisTemplate;

    @Autowired
    protected GeoService geoService;

    @MockBean
    protected GeoClient geoClient;

    @Container
    protected static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:latest"))
                    .withExposedPorts(6379)
                    .withReuse(true);

    @DynamicPropertySource
    protected static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }


    @BeforeEach
    public void before() throws Exception{
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        Mockito.when(geoClient.getCountries()).thenReturn(createCountries());
        Mockito.when(geoClient.getCountryByIdCountryOfHhApi("1")).thenReturn(createDataOfCountry());
        Mockito.when(geoClient.getCountryByIdCountryOfHhApi("5")).thenReturn(new CountryDataFromHhApi(
                5L,
                null,
                "Country 2",
                List.of()));

    }


}
