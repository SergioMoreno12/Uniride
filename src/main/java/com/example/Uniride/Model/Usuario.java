package com.example.Uniride.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false, unique = true, length = 200)
    private String correo;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false, length = 200)
    private String contrasena;

    @Column(nullable = false, length = 20)
    private String rol = "pasajero";

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "foto_perfil", length = 500)
    private String fotoPerfil;
}