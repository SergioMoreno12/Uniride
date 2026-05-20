package com.example.Uniride.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "viaje")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viaje")
    private Long idViaje;

    @Column(nullable = false, length = 200)
    private String origen;

    @Column(nullable = false, length = 200)
    private String destino;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "hora_llegada")
    private LocalDateTime horaLlegada;

    @Column(nullable = false)
    private Double costo;

    @Column(nullable = false, length = 20)
    private String estado = "disponible";

    @Column(name = "descripcion_punto", length = 500)
    private String descripcionPunto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehiculo", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Sede sede;
}