package com.example.Uniride.Service;

import com.example.Uniride.DTO.CalificacionDTO;
import com.example.Uniride.Model.Calificacion;
import java.util.List;

public interface CalificacionService {
    Calificacion guardar(CalificacionDTO dto);
    List<Calificacion> buscarPorConductor(Long idConductor);
    Double promedioConductor(Long idConductor);
    boolean yaCalificada(Long idReserva);
}