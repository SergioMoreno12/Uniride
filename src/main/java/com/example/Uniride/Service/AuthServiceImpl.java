package com.example.Uniride.Service;

import com.example.Uniride.DTO.LoginDTO;
import com.example.Uniride.DTO.LoginResponseDTO;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public LoginResponseDTO login(LoginDTO dto) {

        // Buscar usuario por correo
        Usuario u = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new RuntimeException("Correo o contraseña incorrectos"));

        // Verificar contraseña
        if (!encoder.matches(dto.getContrasena(), u.getContrasena()))
            throw new RuntimeException("Correo o contraseña incorrectos");

        // Verificar que la cuenta esté activa
        if (Boolean.FALSE.equals(u.getActivo()))
            throw new RuntimeException(
                    "Tu cuenta ha sido desactivada. Contacta al administrador.");

        // Retornar respuesta con datos del usuario
        return new LoginResponseDTO(
                "Login exitoso",
                u.getRol(),
                u.getIdUsuario(),
                u.getNombre(),
                u.getFotoPerfil()
        );
    }
}