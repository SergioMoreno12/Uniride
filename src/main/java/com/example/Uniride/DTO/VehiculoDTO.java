package com.example.Uniride.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class VehiculoDTO {
    private String placa;
    private String marca;
    private String modelo;
    private Integer capacidad;
    private Long idUsuario;
}