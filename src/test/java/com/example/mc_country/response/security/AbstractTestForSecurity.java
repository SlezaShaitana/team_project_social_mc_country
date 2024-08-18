package com.example.mc_country.response.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class AbstractTestForSecurity {

    protected String normalToken;
    protected String tokenWithErrorPayload;

    @BeforeEach
    public void setUp() throws Exception{
        String id = UUID.randomUUID().toString().replace("-", "");
        Date now = new Date();
        Date exp = Date.from(LocalDateTime.now().plusMinutes(30)
                .atZone(ZoneId.systemDefault()).toInstant());
        String secret = "secretoiujfgzxcvhjkoiuytdrsdxcvb784653845125hgfdsdxcgvbjnklkjhgf";

        normalToken = Jwts.builder()
                .setId(id)
                .setIssuer("Stormpath")
                .setSubject("msilverman")
                .claim("id", "924d6bad-0f7c-42dd-82fe-7cda182b6dfa")
                .claim("email", "email@yandex.ru")
                .claim("roles", List.of("user", "admin"))
                .setIssuedAt(now)
                .setNotBefore(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        tokenWithErrorPayload = Jwts.builder()
                .setId(id)
                .setIssuer("Darth Vader")
                .setSubject("victory of the sith")
                .setIssuedAt(now)
                .setNotBefore(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


}
