package com.example.Uniride.DTO;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class ReporteDTO {
    private String titulo;
    private String descripcion;
    private String estado;
    private LocalDate fechaReporte;
    private Long idUsuario;
}