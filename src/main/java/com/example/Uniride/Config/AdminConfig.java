package com.example.Uniride.Config;

import com.example.Uniride.Model.AdminCredencial;
import com.example.Uniride.Repository.AdminCredencialRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AdminConfig implements ApplicationRunner {

    private final AdminCredencialRepository adminRepository;
    private final PasswordEncoder encoder;

    @Value("${admin.correo:admin@uniride.edu.co}")
    private String adminCorreo;

    @Value("${admin.contrasena:admin123}")
    private String adminContrasena;

    @Value("${admin.nombre:Administrador UniRide}")
    private String adminNombre;

    public AdminConfig(AdminCredencialRepository adminRepository, PasswordEncoder encoder) {
        this.adminRepository = adminRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        Optional<AdminCredencial> existente = adminRepository.findByCorreo(adminCorreo);

        if (existente.isPresent()) {
            AdminCredencial admin = existente.get();
            admin.setContrasena(encoder.encode(adminContrasena));
            admin.setActivo(true);
            adminRepository.save(admin);
            System.out.println("✅ Admin actualizado en admin_config: " + adminCorreo);
        } else {
            AdminCredencial admin = new AdminCredencial();
            admin.setCorreo(adminCorreo);
            admin.setNombre(adminNombre);
            admin.setContrasena(encoder.encode(adminContrasena));
            admin.setActivo(true);
            admin.setFechaCreacion(LocalDateTime.now());
            adminRepository.save(admin);
            System.out.println("✅ Admin creado en admin_config: " + adminCorreo);
        }
    }
}