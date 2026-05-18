package com.example.Uniride.Repository;

import com.example.Uniride.Model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {

    @Query(value = "SELECT * FROM sede WHERE ciudad = :ciudad", nativeQuery = true)
    List<Sede> findByciudad(String ciudad);
}