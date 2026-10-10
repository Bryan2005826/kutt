package com.jostech.emilker.config;

import com.jostech.emilker.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // Lista separada por comas; sale de app.cors.origins (variable de entorno CORS_ORIGINS
    // en un servidor real). En local, por defecto, solo el frontend en localhost:4200.
    @Value("${app.cors.origins}")
    private String corsOrigins;

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
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                // Protegido por clave secreta (SEED_KEY) dentro del propio controlador, no por rol
                .requestMatchers(HttpMethod.POST, "/api/admin/cargar-negocios").permitAll()

                // Panel exclusivo del dueno de la plataforma Kutt (no de un negocio en particular);
                // va antes del permitAll de GET para que quede protegido tambien en lectura
                .requestMatchers("/api/plataforma/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/api/negocios/mi-negocio").hasRole("ADMIN_NEGOCIO")

                .requestMatchers(HttpMethod.GET, "/api/**").permitAll()

                // El cliente puede agendar, cancelar, reprogramar y pagar su propia cita, o comprar productos
                .requestMatchers(HttpMethod.POST, "/api/citas").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/cancelar").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/reprogramar").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/citas/*/pagar").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/ventas/compra").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/ventas/*/pagar").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/resenas").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/favoritos").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/favoritos").authenticated()

                // Solo el Super Admin acepta/rechaza/finaliza citas, gestiona inventario, clientes,
                // ventas manuales, servicios/adicionales, metodos de pago y configuracion
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/confirmar").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/rechazar").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/finalizar").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.POST, "/api/productos").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.POST, "/api/archivos/subir").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.PUT, "/api/productos/*").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/*").hasRole("ADMIN_NEGOCIO")
                // El cliente edita su propio perfil desde Mi perfil; el admin edita a sus clientes
                .requestMatchers(HttpMethod.PUT, "/api/clientes/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/ventas").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.POST, "/api/adicionales").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.PUT, "/api/adicionales/*").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.DELETE, "/api/adicionales/*").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.POST, "/api/metodos-pago").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.DELETE, "/api/metodos-pago/*").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.POST, "/api/barberos").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.PUT, "/api/barberos/*").hasRole("ADMIN_NEGOCIO")
                .requestMatchers(HttpMethod.DELETE, "/api/barberos/*").hasRole("ADMIN_NEGOCIO")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(corsOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
