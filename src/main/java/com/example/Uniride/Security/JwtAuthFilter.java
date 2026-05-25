package com.example.Uniride.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.esValido(token)) {
                try {
                    Long idUsuario = jwtUtil.extraerIdUsuario(token);
                    String rol     = jwtUtil.extraerRol(token);

                    // Establecer la autenticación en el contexto de seguridad
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    idUsuario,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase()))
                            );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    logger.debug("JWT válido para usuario id={}, rol={}", idUsuario, rol);
                } catch (Exception e) {
                    logger.warn("Error al procesar JWT: {}", e.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}