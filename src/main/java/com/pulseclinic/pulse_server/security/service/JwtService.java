package com.pulseclinic.pulse_server.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwt_secret;

    @Value("${jwt.expiration}")
    private long jwt_expiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // sub = username
    }

    // generic
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(Map<String,Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().addClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, getSignInKey())
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration); // get expiration from claims
    }

    public long getRemainingTtlSeconds(String token) {
        Date exp = extractExpiration(token);
        long diff = exp.getTime() - System.currentTimeMillis();
        return diff > 0 ? diff / 1000 : 0;
    }


    // create key for signing tokens
    private Key getSignInKey() {
        // decode the secret via base64 decode algo
        // after: Byte array:    [72, 101, 108, 108, 111, 87, 111, 114, 108, 100]
        byte[] keyBytes = Base64.getDecoder().decode(jwt_secret);
        // creating key
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
