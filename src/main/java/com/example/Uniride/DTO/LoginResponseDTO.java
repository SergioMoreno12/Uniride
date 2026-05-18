package com.example.Uniride.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LoginResponseDTO {
    private String mensaje;
    private String rol;       // "conductor", "pasajero" o "administrador"
    private Long idUsuario;   // null si es administrador
    private String nombre;
}