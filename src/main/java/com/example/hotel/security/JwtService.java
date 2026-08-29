package com.example.hotel.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    // HS256 requiere una key de al menos 256 bits (32 bytes).
    private static final int MIN_SECRET_BYTES = 32;

    @Value("${jwt.secret}")
    private String secret;

    // IMPORTANTE: debe estar en milisegundos (ej: 3600000 = 1 hora).
    @Value("${jwt.expiration}")
    private long expiration;

    private Key signingKey;

    /**
     * Valida el secret al arrancar la aplicación (fail-fast) en vez de
     * descubrir un secret débil o mal configurado recién en el primer login.
     */
    @PostConstruct
    private void init() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "jwt.secret debe tener al menos " + MIN_SECRET_BYTES +
                    " bytes para HS256. Genera uno con: openssl rand -base64 32");
        }
        if (expiration <= 0) {
            throw new IllegalStateException("jwt.expiration debe ser mayor a 0 (en milisegundos)");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el email (subject) del token.
     *
     * @throws InvalidTokenException si el token es inválido, está expirado
     *         o fue manipulado.
     */
    public String extractEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token JWT inválido: {}", e.getMessage());
            throw new InvalidTokenException("Token inválido o expirado", e);
        }
    }
}