package com.example.Uniride.DTO;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class ViajeDTO {
    private String origen;
    private String destino;
    private LocalDateTime fechaHora;
    private LocalDateTime horaLlegada;
    private Double costo;
    private String estado;
    private String descripcionPunto;
    private Long idVehiculo;
    private Long idSede;
}