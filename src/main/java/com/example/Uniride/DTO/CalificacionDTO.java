package com.example.Uniride.DTO;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class CalificacionDTO {
    private Integer puntuacion;
    private String comentario;
    private LocalDate fechaCalificacion;
    private Long idReserva;
    private Long idConductor;
    private Long idPasajero;
}