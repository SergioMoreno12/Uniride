package com.example.Uniride.Repository;

import com.example.Uniride.Model.AdminCredencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminCredencialRepository extends JpaRepository<AdminCredencial, Long> {

    @Query(value = "SELECT * FROM admin_config WHERE correo = :correo", nativeQuery = true)
    Optional<AdminCredencial> findByCorreo(String correo);
}