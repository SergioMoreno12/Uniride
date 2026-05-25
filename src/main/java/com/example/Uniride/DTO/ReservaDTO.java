package com.example.Uniride.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class ReservaDTO {

    private LocalDate fechaReserva;
    private Boolean confirmada;

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El id del viaje es obligatorio")
    private Long idViaje;
}