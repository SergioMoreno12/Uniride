package com.example.Uniride.Service;

import com.example.Uniride.DTO.LoginDTO;
import com.example.Uniride.DTO.LoginResponseDTO;
import com.example.Uniride.Model.AdminCredencial;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.AdminCredencialRepository;
import com.example.Uniride.Repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository         usuarioRepository;
    private final AdminCredencialRepository adminRepository;
    private final BCryptPasswordEncoder     encoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           AdminCredencialRepository adminRepository) {
        this.usuarioRepository = usuarioRepository;
        this.adminRepository   = adminRepository;
    }

    @Override
    public LoginResponseDTO login(LoginDTO dto) {

        // ── 1. Buscar primero en admin_config ──────────────────────────
        Optional<AdminCredencial> adminOpt = adminRepository.findByCorreo(dto.getCorreo());

        if (adminOpt.isPresent()) {
            AdminCredencial admin = adminOpt.get();

            if (!encoder.matches(dto.getContrasena(), admin.getContrasena()))
                throw new RuntimeException("Correo o contraseña incorrectos");

            if (Boolean.FALSE.equals(admin.getActivo()))
                throw new RuntimeException("La cuenta de administrador está desactivada.");

            return new LoginResponseDTO(
                    "Login exitoso",
                    "administrador",
                    admin.getIdAdmin(),
                    admin.getNombre(),
                    null
            );
        }

        // ── 2. Si no es admin, buscar en tabla usuario ─────────────────
        Usuario u = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new RuntimeException("Correo o contraseña incorrectos"));

        if (!encoder.matches(dto.getContrasena(), u.getContrasena()))
            throw new RuntimeException("Correo o contraseña incorrectos");

        // Capa extra: bloquear si alguien tiene rol admin en tabla usuario
        if ("administrador".equals(u.getRol()))
            throw new RuntimeException("Correo o contraseña incorrectos");

        if (Boolean.FALSE.equals(u.getActivo()))
            throw new RuntimeException("Tu cuenta ha sido desactivada. Contacta al administrador.");

        return new LoginResponseDTO(
                "Login exitoso",
                u.getRol(),
                u.getIdUsuario(),
                u.getNombre(),
                u.getFotoPerfil()
        );
    }
}