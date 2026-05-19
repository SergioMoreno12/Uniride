package com.example.Uniride.DTO;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class UsuarioDTO {
    private String nombre;
    private String correo;
    private String contrasena;
    private String telefono;
    private LocalDate fechaRegistro;
    private String rol;
}