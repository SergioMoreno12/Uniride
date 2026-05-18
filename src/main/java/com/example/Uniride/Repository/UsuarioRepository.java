package com.example.Uniride.Repository;

import com.example.Uniride.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "SELECT * FROM usuario WHERE correo = :correo", nativeQuery = true)
    Optional<Usuario> findByCorreo(String correo);
}