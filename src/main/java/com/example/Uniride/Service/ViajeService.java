package com.example.Uniride.Service;

import com.example.Uniride.DTO.ViajeDTO;
import com.example.Uniride.Model.Viaje;
import java.util.List;

public interface ViajeService {
    List<Viaje> listarTodos();
    Viaje buscarPorId(Long id);
    Viaje guardar(ViajeDTO dto);
    Viaje actualizar(Long id, ViajeDTO dto);
    void eliminar(Long id);
    List<Viaje> buscarPorSede(Long idSede);
    List<Viaje> buscarPorEstado(String estado);
    List<Viaje> buscarPorVehiculo(Long idVehiculo);
    List<Viaje> buscarPorCiudad(String ciudad);
    Viaje cancelar(Long id);
    Viaje completar(Long id);
}