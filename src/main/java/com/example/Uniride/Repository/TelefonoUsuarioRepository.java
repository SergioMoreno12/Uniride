package com.example.Uniride.Repository;

import com.example.Uniride.Model.TelefonoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TelefonoUsuarioRepository extends JpaRepository<TelefonoUsuario, Long> {

    @Query(value = "SELECT * FROM telefono_usuario WHERE id_usuario = :idUsuario", nativeQuery = true)
    List<TelefonoUsuario> findByIdUsuario(Long idUsuario);

    @Modifying
    @Query(value = "DELETE FROM telefono_usuario WHERE id_usuario = :idUsuario", nativeQuery = true)
    void deleteByIdUsuario(Long idUsuario);
}