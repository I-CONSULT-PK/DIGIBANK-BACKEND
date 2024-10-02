package com.admin_service.service.serviceImpl;
import com.admin_service.entity.User;
import com.admin_service.exception.CustomJwtException;
import com.admin_service.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public String generateToken(String username, Set<GrantedAuthority> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
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
    public Claims getClaims(String token) {
        try{
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException exception){
        //throw new RuntimeException(exception);
        throw new CustomJwtException("JWT token has expired. Please login again.", exception);
        }catch (SignatureException e) {
        // Handle invalid JWT signature (forged or tampered token)
        throw new CustomJwtException("Invalid JWT signature.", e);
        } catch (MalformedJwtException e) {
        // Handle malformed tokens (incorrect format)
        throw new CustomJwtException("Malformed JWT token.", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
         //   throw new CustomJwtException("Invalid JWT token", e);
        }
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

