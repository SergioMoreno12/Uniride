package com.example.Uniride.Service;

import com.example.Uniride.DTO.LoginDTO;
import com.example.Uniride.DTO.LoginResponseDTO;
import com.example.Uniride.Model.AdminCredencial;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.AdminCredencialRepository;
import com.example.Uniride.Repository.UsuarioRepository;
import com.example.Uniride.Security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    // ✅ Logger profesional — Recomendación del profesor
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UsuarioRepository         usuarioRepository;
    private final AdminCredencialRepository adminRepository;
    private final PasswordEncoder           encoder;
    private final JwtUtil                   jwtUtil;   // ✅ JWT — Recomendación del profesor

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           AdminCredencialRepository adminRepository,
                           PasswordEncoder encoder,
                           JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.adminRepository   = adminRepository;
        this.encoder           = encoder;
        this.jwtUtil           = jwtUtil;
    }

    @Override
    public LoginResponseDTO login(LoginDTO dto) {

        if (dto.getCorreo() == null || dto.getContrasena() == null)
            throw new RuntimeException("Correo y contraseña son obligatorios");

        String correo = dto.getCorreo().trim().toLowerCase();
        logger.info("Intento de login para correo: {}", correo);

        // ── 1. Buscar primero en admin_config ──────────────────────────
        Optional<AdminCredencial> adminOpt = adminRepository.findByCorreo(correo);

        if (adminOpt.isPresent()) {
            AdminCredencial admin = adminOpt.get();

            if (!encoder.matches(dto.getContrasena(), admin.getContrasena())) {
                logger.warn("Contraseña incorrecta para admin: {}", correo);
                throw new RuntimeException("Correo o contraseña incorrectos");
            }

            if (Boolean.FALSE.equals(admin.getActivo()))
                throw new RuntimeException("La cuenta de administrador está desactivada.");

            // ✅ Generar JWT para el admin
            String token = jwtUtil.generarToken(admin.getIdAdmin(), "administrador");
            logger.info("Login exitoso de administrador id={}", admin.getIdAdmin());

            return new LoginResponseDTO(
                    "Login exitoso",
                    "administrador",
                    admin.getIdAdmin(),
                    admin.getNombre(),
                    null,
                    token   // ✅ token JWT
            );
        }

        // ── 2. Si no es admin, buscar en tabla usuario ─────────────────
        Usuario u = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Correo o contraseña incorrectos"));

        if (!encoder.matches(dto.getContrasena(), u.getContrasena())) {
            logger.warn("Contraseña incorrecta para usuario id={}", u.getIdUsuario());
            throw new RuntimeException("Correo o contraseña incorrectos");
        }

        // Evitar que rol "administrador" exista en tabla usuario
        if ("administrador".equals(u.getRol()))
            throw new RuntimeException("Correo o contraseña incorrectos");

        if (Boolean.FALSE.equals(u.getActivo()))
            throw new RuntimeException("Tu cuenta ha sido desactivada. Contacta al administrador.");

        // ✅ Generar JWT para el usuario
        String token = jwtUtil.generarToken(u.getIdUsuario(), u.getRol());
        logger.info("Login exitoso de usuario id={}, rol={}", u.getIdUsuario(), u.getRol());

        return new LoginResponseDTO(
                "Login exitoso",
                u.getRol(),
                u.getIdUsuario(),
                u.getNombre(),
                u.getFotoPerfil(),
                token   // ✅ token JWT
        );
    }
}