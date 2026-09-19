package com.jostech.emilker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Clave fija para entorno local / academico.
    // En un entorno real esta clave debe ir en una variable de entorno, nunca en el codigo.
    private final SecretKey clave = Keys.hmacShaKeyFor(
            "emilker-barber-shop-clave-secreta-super-larga-para-firmar-jwt-local".getBytes());

    private final long expiracionMs = 1000L * 60 * 60 * 12; // 12 horas

    public String generarToken(String correo, String rol) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expiracionMs);
        return Jwts.builder()
                .setSubject(correo)
                .claim("rol", rol)
                .setIssuedAt(ahora)
                .setExpiration(expira)
                .signWith(clave, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validarYObtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(clave)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
