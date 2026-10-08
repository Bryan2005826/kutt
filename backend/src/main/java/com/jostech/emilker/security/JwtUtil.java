package com.jostech.emilker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // La clave sale de app.jwt.secret (application.properties), que a su vez
    // toma la variable de entorno JWT_SECRET si existe. Trae un valor por
    // defecto solo para que el proyecto funcione de una en local.
    private final SecretKey clave;

    public JwtUtil(@Value("${app.jwt.secret}") String secreto) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes());
    }

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
