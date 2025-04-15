package com.example.springjwt_db.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpirationMs;

    public String generateJwtToken(UserDetails userDetails) {
        log.info("Gerando token JWT para usuário: {}", userDetails.getUsername());
        try {
            Date now = new Date();
            Date expiration = new Date(System.currentTimeMillis() + jwtExpirationMs);
            log.info("Token vai expirar em: {}", expiration);
            
            String token = Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                    .compact();
            
            log.info("Token gerado com sucesso");
            return token;
        } catch (Exception e) {
            log.error("Erro ao gerar token JWT: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            String username = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            log.info("Username extraído do token: {}", username);
            return username;
        } catch (Exception e) {
            log.error("Erro ao extrair username do token: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateJwtToken(String token) {
        try {
            log.info("Validando token JWT");
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token);
            log.info("Token JWT válido");
            return true;
        } catch (SignatureException e) {
            log.error("Assinatura JWT inválida: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token JWT inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT não suportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Claims JWT vazio: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao validar token JWT: {}", e.getMessage());
        }
        return false;
    }
}
