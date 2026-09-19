package com.jostech.emilker.config;

import com.jostech.emilker.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/clientes/asegurar").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/**").permitAll()

                // El cliente puede agendar, cancelar, reprogramar y pagar su propia cita, o comprar productos
                .requestMatchers(HttpMethod.POST, "/api/citas").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/cancelar").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/reprogramar").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/citas/*/pagar").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/ventas/compra").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/ventas/*/pagar").authenticated()

                // Solo el Super Admin acepta/rechaza/finaliza citas, gestiona inventario, clientes,
                // ventas manuales, servicios/adicionales, metodos de pago y configuracion
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/confirmar").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/rechazar").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/finalizar").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.POST, "/api/productos").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.PUT, "/api/clientes/*").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.POST, "/api/ventas").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.POST, "/api/adicionales").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.PUT, "/api/adicionales/*").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.DELETE, "/api/adicionales/*").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.POST, "/api/metodos-pago").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.DELETE, "/api/metodos-pago/*").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.POST, "/api/barberos").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.PUT, "/api/barberos/*").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.DELETE, "/api/barberos/*").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.PUT, "/api/configuracion").hasRole("ADMIN_NEGOCIO")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
