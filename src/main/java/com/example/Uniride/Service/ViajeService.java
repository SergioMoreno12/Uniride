package com.example.Uniride.Service;

import com.example.Uniride.DTO.ViajeDTO;
import com.example.Uniride.Model.Viaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ViajeService {
    List<Viaje>   listarTodos();
    Page<Viaje>   listarPaginado(Pageable pageable);
    Viaje         buscarPorId(Long id);
    Viaje         guardar(ViajeDTO dto);
    Viaje         actualizar(Long id, ViajeDTO dto);
    void          eliminar(Long id);
    List<Viaje>   buscarPorSede(Long idSede);
    List<Viaje>   buscarPorEstado(String estado);
    List<Viaje>   buscarPorVehiculo(Long idVehiculo);
    List<Viaje>   buscarPorCiudad(String ciudad);
    Viaje         cancelar(Long id);
    Viaje         completar(Long id);
}