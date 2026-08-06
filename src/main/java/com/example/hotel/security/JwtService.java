package com.example.hotel.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import java.security.Key;

@Service
public class JwtService {

        @Value("${jwt.secret}")
        private String secret;

        @Value("${jwt.expiration}")
        private long expiration;

        public String generateToken(String email) {

                Date now = new Date();

                Date expiryDate = new Date(now.getTime() + expiration);

                Key key = Keys.hmacShaKeyFor(
                                secret.getBytes());

                return Jwts.builder()
                                .setSubject(email)
                                .setIssuedAt(now)
                                .setExpiration(expiryDate)
                                .signWith(key, SignatureAlgorithm.HS256)
                                .compact();
        }

        public String extractEmail(String token) {

                Key key = Keys.hmacShaKeyFor(
                                secret.getBytes());

                return Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(token)
                                .getBody()
                                .getSubject();
        }

   

}