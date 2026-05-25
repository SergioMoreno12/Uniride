package com.example.Uniride.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String mensaje;
    private String rol;
    private Long   idUsuario;
    private String nombre;
    private String fotoPerfil;
    // ✅ NUEVO: Token JWT para autenticación en siguientes requests
    private String token;
}