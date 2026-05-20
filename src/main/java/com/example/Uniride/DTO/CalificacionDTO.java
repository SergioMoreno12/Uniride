package com.example.Uniride.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CalificacionDTO {
    private Integer puntuacion;
    private String comentario;
    private Long idReserva;
    private Long idConductor;
    private Long idPasajero;
}