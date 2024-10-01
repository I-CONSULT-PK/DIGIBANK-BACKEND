package com.admin_service.service.serviceImpl;
import com.admin_service.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Component
public class JwtUtil {
    @Autowired
    UserRepository userRepository;


    // private String SECRET_KEY = "my_secret_key"; // Your secret key

    //private final String SECRET_KEY = "your_strong_secret_key"; // Ensure the key is strong
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    //private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // Use this method in your class to return a byte array of the secret key
//    private byte[] getSecretKeyBytes() {
//        return SECRET_KEY.getBytes();
//    }
    private byte[] getSecretKeyBytes() {
        return SECRET_KEY.getEncoded(); // Get the encoded key as a byte array
    }


    // Generate token with roles from DB
    public String generateToken(String username, Set<GrantedAuthority> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours validity
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 2))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Claims claims = getClaims(token);
        return (List<String>) claims.get("roles");
    }

    private boolean findBySessionToken(String sessionToken) {
        return userRepository.isValidToken(sessionToken,System.currentTimeMillis());
//        return UserRepositoryisValidToken(sessionToken, currentTime);
    }
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        if (findBySessionToken(token)) {
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        }
        return false;
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
    public Date getTokenExpireTime(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

