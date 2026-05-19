package com.example.Uniride.Service;

import com.example.Uniride.DTO.ReporteDTO;
import com.example.Uniride.Model.Reporte;
import java.util.List;

public interface ReporteService {
    List<Reporte> listarTodos();
    Reporte buscarPorId(Long id);
    Reporte guardar(ReporteDTO dto);
    Reporte actualizar(Long id, ReporteDTO dto);
    void eliminar(Long id);
    List<Reporte> buscarPorEstado(String estado);
    List<Reporte> buscarPorUsuario(Long idUsuario);
}