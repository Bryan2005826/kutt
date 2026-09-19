package com.jostech.emilker.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtUtil.validarYObtenerClaims(token);
                String correo = claims.getSubject();
                String rol = claims.get("rol", String.class);

                List<SimpleGrantedAuthority> autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + rol));
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(correo, null, autoridades);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception tokenInvalido) {
                // Token invalido o expirado: se deja sin autenticar y Spring Security
                // decide segun la regla de la ruta (permitAll, authenticated, hasRole).
            }
        }

        filterChain.doFilter(request, response);
    }
}
