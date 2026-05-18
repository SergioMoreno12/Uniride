package com.example.Uniride.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminConfig {

    @Value("${admin.correo}")
    private String correoAdmin;

    @Value("${admin.contrasena}")
    private String contrasenaAdmin;

    public String getCorreoAdmin() {
        return correoAdmin;
    }

    public String getContrasenaAdmin() {
        return contrasenaAdmin;
    }
}