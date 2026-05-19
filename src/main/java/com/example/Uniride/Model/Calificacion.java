package com.example.Uniride.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "calificacion")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calificacion")
    private Long idCalificacion;

    @Column(nullable = false)
    private Integer puntuacion;

    @Column(length = 300)
    private String comentario;

    @Column(name = "fecha_calificacion", nullable = false)
    private LocalDate fechaCalificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ToString.Exclude
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conductor", nullable = false)
    @JsonIgnoreProperties({"telefonos", "vehiculos", "hibernateLazyInitializer", "handler"})
    @ToString.Exclude
    private Usuario conductor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pasajero", nullable = false)
    @JsonIgnoreProperties({"telefonos", "vehiculos", "hibernateLazyInitializer", "handler"})
    @ToString.Exclude
    private Usuario pasajero;
}