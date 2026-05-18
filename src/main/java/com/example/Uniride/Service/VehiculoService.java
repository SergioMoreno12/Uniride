package com.example.Uniride.Service;

import com.example.Uniride.DTO.VehiculoDTO;
import com.example.Uniride.Model.Vehiculo;
import java.util.List;

public interface VehiculoService {
    List<Vehiculo> listarTodos();
    Vehiculo buscarPorId(Long id);
    Vehiculo guardar(VehiculoDTO dto);
    Vehiculo actualizar(Long id, VehiculoDTO dto);
    void eliminar(Long id);
    List<Vehiculo> buscarPorUsuario(Long idUsuario);
}