package com.example.Uniride.Service;

import com.example.Uniride.DTO.SedeDTO;
import com.example.Uniride.Model.Sede;
import java.util.List;

public interface SedeService {
    List<Sede> listarTodas();
    Sede buscarPorId(Long id);
    Sede guardar(SedeDTO dto);
    Sede actualizar(Long id, SedeDTO dto);
    void eliminar(Long id);
    List<Sede> buscarPorCiudad(String ciudad);
}