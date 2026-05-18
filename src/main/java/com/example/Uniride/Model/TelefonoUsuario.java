package com.example.Uniride.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "telefono_usuario")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class TelefonoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_telefono")
    private Long idTelefono;

    @Column(nullable = false, length = 20)
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnoreProperties({"telefonos", "vehiculos", "hibernateLazyInitializer", "handler"})
    @ToString.Exclude
    private Usuario usuario;
}