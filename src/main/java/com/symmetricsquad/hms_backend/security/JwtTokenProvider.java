package com.symmetricsquad.hms_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String SECRET = "hms-backend";
    private static final long TOKEN_VALIDITY_MILLIS = 1440L * 60L * 1000L;

    private final Key key;

    public JwtTokenProvider() {
        byte[] keyBytes = new byte[64];
        byte[] secretBytes = SECRET.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(secretBytes, 0, keyBytes, 0, Math.min(secretBytes.length, keyBytes.length));
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String subject, String role, Long userId) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiry = Date.from(now.plusMillis(TOKEN_VALIDITY_MILLIS));

        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(issuedAt)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> validateAndGetClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
