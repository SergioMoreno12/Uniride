package com.example.Uniride.DTO;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class ReservaDTO {
    private LocalDate fechaReserva;
    private Boolean confirmada;
    private Long idUsuario;
    private Long idViaje;
}