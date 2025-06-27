package com.springroot.free.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class JwtService {

    private final String secretKey;
    private final String refreshSecretKey;
    @Autowired
    private MyUserDetailsService userDetailsService;

    public JwtService() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");

            SecretKey sk = keyGenerator.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());

            SecretKey refreshKey = keyGenerator.generateKey();
            refreshSecretKey = Base64.getEncoder().encodeToString(refreshKey.getEncoded());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String username, String email) {
        return createToken(username, email, secretKey, 30 * 60 * 1000);
    }

    public String generateRefreshToken(String username) {
        return createToken(username, null, refreshSecretKey, 30 * 24 * 60 * 60 * 1000);
    }

    private String createToken(String username, String email, String key, long duration) {
        Map<String, Object> claims = new HashMap<>();
        if (email != null) claims.put("email", email);

        long nowMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .addClaims(claims)
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(nowMillis + duration))
                .signWith(getKey(key))
                .compact();
    }

    private Key getKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token, boolean isRefreshToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey(isRefreshToken ? refreshSecretKey : secretKey))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid or expired JWT token", e);
        }
    }

    public boolean validateToken(String token, UserDetails userDetails, boolean isRefreshToken) {
        String username = extractUsername(token, isRefreshToken);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, isRefreshToken);
    }

    private boolean isTokenExpired(String token, boolean isRefreshToken) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getKey(isRefreshToken ? refreshSecretKey : secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {

            String username = extractUsername(refreshToken, true);


            UserDetails userDetails = userDetailsService.loadUserByUsername(username);


            return username.equals(userDetails.getUsername()) && !isTokenExpired(refreshToken, true);
        } catch (JwtException | IllegalArgumentException e) {

            e.printStackTrace();


            return false;
        }
    }

}
