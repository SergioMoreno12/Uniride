package com.example.Uniride.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ViajeDTO {

    @NotBlank(message = "El origen es obligatorio")
    private String origen;

    @NotBlank(message = "El destino es obligatorio")
    private String destino;

    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime fechaHora;

    private LocalDateTime horaLlegada;

    @NotNull(message = "El costo es obligatorio")
    @Min(value = 0, message = "El costo no puede ser negativo")
    private Double costo;

    private String estado;

    private String descripcionPunto;

    private String tipoViaje;

    @NotNull(message = "El vehículo es obligatorio")
    private Long idVehiculo;

    @NotNull(message = "La sede es obligatoria")
    private Long idSede;
}