package com.example.mc_country.repositories;

import com.example.mc_country.entity.City;
import com.example.mc_country.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CityRepository  extends JpaRepository<City, String> {
    @Query("SELECT c FROM City AS c WHERE c.country =:country")
    List<City> findByCountry(Country country);
}
