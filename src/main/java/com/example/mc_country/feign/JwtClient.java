package com.example.mc_country.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "jwt",
        url = "http://79.174.80.200:8086/api/v1/auth")
public interface JwtClient {
    @GetMapping("/check-validation")
    Boolean validateToken(@RequestParam("token") String token);

}
