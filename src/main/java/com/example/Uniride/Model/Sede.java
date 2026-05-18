package com.example.Uniride.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sede")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sede")
    private Long idSede;

    @Column(name = "nombre_sede", nullable = false, length = 150)
    private String nombreSede;

    @Column(nullable = false, length = 100)
    private String ciudad;
}