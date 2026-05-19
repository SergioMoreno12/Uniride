package com.example.Uniride.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class CambiarContrasenaDTO {
    private String contrasenaActual;
    private String contrasenaNueva;
}