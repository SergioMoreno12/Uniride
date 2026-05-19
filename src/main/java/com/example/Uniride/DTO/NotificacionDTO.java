package com.example.Uniride.DTO;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class NotificacionDTO {
    private String titulo;
    private String mensaje;
    private String destinatarios;
    private LocalDateTime fechaEnvio;
}