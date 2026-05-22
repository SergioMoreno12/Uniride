package com.example.Uniride.Config;

import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class AdminConfig implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${admin.correo:admin@uniride.edu.co}")
    private String adminCorreo;

    @Value("${admin.contrasena:admin123}")
    private String adminContrasena;

    public AdminConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Optional<Usuario> existente = usuarioRepository.findByCorreo(adminCorreo);

        if (existente.isPresent()) {
            // Actualizar contraseña del admin existente con hash correcto
            Usuario admin = existente.get();
            admin.setContrasena(encoder.encode(adminContrasena));
            admin.setActivo(true);
            admin.setRol("administrador");
            usuarioRepository.save(admin);
            System.out.println("✅ Admin actualizado: " + adminCorreo);
        } else {
            // Crear admin si no existe
            Usuario admin = new Usuario();
            admin.setCorreo(adminCorreo);
            admin.setNombre("Administrador UniRide");
            admin.setContrasena(encoder.encode(adminContrasena));
            admin.setRol("administrador");
            admin.setActivo(true);
            admin.setFechaRegistro(LocalDate.now());
            usuarioRepository.save(admin);
            System.out.println("✅ Admin creado: " + adminCorreo);
        }
    }
}