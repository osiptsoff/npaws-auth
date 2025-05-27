package ru.osiptsoff.npaws_auth.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.osiptsoff.npaws_auth.model.Role;
import ru.osiptsoff.npaws_auth.model.User;

@Component
@RequiredArgsConstructor
public class JwtUtility {
    private final KeyStore keyStore;

    @Value("${app.config.accessLifespawnSec}")
    @Setter
    private Integer accesLifespawn;
    @Value("${app.config.refreshLifespawnSec}")
    @Setter
    @Getter
    private Integer refreshLifespawn;

    public Key getAccessPublicKey() {
        return keyStore.getAccessPublicKey();
    }

    public Integer getRefreshTokenLifespawn() {
        return refreshLifespawn;
    }

    public String generateRefreshToken(User userDetails) {
        return generateToken(userDetails, keyStore.getRefreshKey(), refreshLifespawn);
    }

    public String generateAccessToken(User userDetails) {
        return generateToken(userDetails, keyStore.getAccessPrivateKey(), accesLifespawn);
    }

    public User parseAndValidateRefreshToken(String refreshToken) throws ExpiredJwtException, JwtException {
        return generateUserDetails(refreshToken, keyStore.getRefreshKey());
    }

    private User generateUserDetails(String token, SecretKey key) throws ExpiredJwtException, JwtException {
        Claims claims = parseToken(token, key);

        areClaimsValid(claims);

        User user = new User();
        user.setId(UUID.fromString(claims.get("userId").toString()));
        user.setUsername(claims.getSubject());
        user.setRoles(claims
            .getAudience()
            .stream()
            .map(a -> {
                Role role = new Role();
                role.setName(a);
                return role;
            })
            .collect(Collectors.toSet())
        );

        return user;
    }

    private String generateToken(User user, Key key, Integer lifespawn) {
        Date now = new Date();

        return Jwts.builder()
            .issuedAt(now)
            .expiration(new Date(now.getTime() + lifespawn * 1000))
            .subject(user.getUsername())
            .claim("userId", user.getId())
            .audience()
            .add(user.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet()))
            .and()
            .signWith(key)
            .compact();
    }

    private Claims parseToken(String token, SecretKey key) throws ExpiredJwtException, JwtException {
        try {
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch(ExpiredJwtException e) {
            throw e;
        } catch(JwtException | IllegalArgumentException e) {
            throw new JwtException("Failed to parse token");
        }
    }

    private boolean areClaimsValid(Claims claims) throws JwtException {
        if(claims.getIssuedAt() == null
                || claims.getExpiration() == null
                || claims.getSubject() == null
                || claims.getAudience() == null) {
            throw new JwtException("Token has invalid content");
        }

        return true;
    }
}