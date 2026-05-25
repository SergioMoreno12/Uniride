package com.example.Uniride.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    private Key getKey() {
        byte[] keyBytes = secret.getBytes();
        // Asegurar que la clave tenga al menos 256 bits (32 bytes) para HS256
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            return Keys.hmacShaKeyFor(padded);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /** Genera un JWT con el id del usuario y su rol como claims. */
    public String generarToken(Long idUsuario, String rol) {
        return Jwts.builder()
                .setSubject(String.valueOf(idUsuario))
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Extrae el id del usuario del token. */
    public Long extraerIdUsuario(String token) {
        String subject = getClaims(token).getSubject();
        return Long.parseLong(subject);
    }

    /** Extrae el rol del token. */
    public String extraerRol(String token) {
        return getClaims(token).get("rol", String.class);
    }

    /** Valida que el token sea correcto y no haya expirado. */
    public boolean esValido(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("Token JWT no soportado: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.warn("Token JWT malformado: {}", e.getMessage());
        } catch (Exception e) {
            logger.warn("Token JWT inválido: {}", e.getMessage());
        }
        return false;
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}