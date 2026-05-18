package com.example.Uniride.DTO;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class ViajeDTO {
    private String origen;
    private String destino;
    private LocalDateTime fechaHora;
    private BigDecimal costo;
    private String estado;
    private Long idVehiculo;
    private Long idSede;
}