package com.example.mc_country.repositories;

import com.example.mc_country.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CountryRepository extends JpaRepository<Country, String> {
}
