package com.example.mc_country.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "jwt",
        url = "???")
public interface JwtClient {

    default Boolean validateToken(String token){
        return true;
    };
}
