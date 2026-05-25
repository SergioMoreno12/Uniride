package com.example.Uniride.Config;

import com.example.Uniride.Security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CSRF desactivado (API REST stateless)
                .csrf(csrf -> csrf.disable())
                // ✅ CORS con configuración de CorsConfig
                .cors(Customizer.withDefaults())
                // ✅ Sin sesiones HTTP (stateless JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas de autenticación
                        .requestMatchers("/api/auth/**").permitAll()
                        // Registro de nuevos usuarios (público)
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        // Viajes y sedes son públicos para lectura (Splash screen + browse)
                        .requestMatchers(HttpMethod.GET, "/api/viajes", "/api/viajes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sedes", "/api/sedes/**").permitAll()
                        // Swagger / OpenAPI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // Todo lo demás requiere autenticación JWT
                        .anyRequest().authenticated()
                )
                // ✅ Añadir el filtro JWT antes del filtro estándar de autenticación
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}