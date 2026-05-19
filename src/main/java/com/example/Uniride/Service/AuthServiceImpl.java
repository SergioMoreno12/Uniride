package com.example.Uniride.Service;

import com.example.Uniride.Config.AdminConfig;
import com.example.Uniride.DTO.LoginDTO;
import com.example.Uniride.DTO.LoginResponseDTO;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminConfig adminConfig;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           AdminConfig adminConfig) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminConfig = adminConfig;
    }

    @Override
    public LoginResponseDTO login(LoginDTO dto) {

        // 1. Verificar si es el administrador
        if (dto.getCorreo().equals(adminConfig.getCorreoAdmin()) &&
                dto.getContrasena().equals(adminConfig.getContrasenaAdmin())) {
            return new LoginResponseDTO(
                    "Bienvenido administrador",
                    "administrador",
                    null,
                    "Administrador"
            );
        }

        // 2. Buscar usuario en la base de datos
        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new RuntimeException("Correo o contraseña incorrectos."));

        // 3. Verificar contraseña encriptada
        if (!passwordEncoder.matches(dto.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Correo o contraseña incorrectos.");
        }

        // 4. Devolver respuesta con el rol real del usuario (conductor o pasajero)
        return new LoginResponseDTO(
                "Inicio de sesion exitoso",
                usuario.getRol(),
                usuario.getIdUsuario(),
                usuario.getNombre()
        );
    }
}