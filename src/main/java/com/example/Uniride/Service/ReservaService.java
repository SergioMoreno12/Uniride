package com.example.Uniride.Service;

import com.example.Uniride.DTO.ReservaDTO;
import com.example.Uniride.Model.Reserva;
import java.util.List;

public interface ReservaService {
    List<Reserva> listarTodas();
    Reserva buscarPorId(Long id);
    Reserva guardar(ReservaDTO dto);
    Reserva actualizar(Long id, ReservaDTO dto);
    void eliminar(Long id);
    List<Reserva> buscarPorUsuario(Long idUsuario);
    List<Reserva> buscarPorViaje(Long idViaje);
    List<Reserva> confirmadasPorViaje(Long idViaje);
    Reserva confirmar(Long id);
    void cancelar(Long id);
}