package com.example.Uniride.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "reporte")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Long idReporte;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String estado = "pendiente";

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDate fechaReporte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(
            {"telefonos", "vehiculos", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;
}