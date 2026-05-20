package com.example.Uniride.Service;

import com.example.Uniride.DTO.NotificacionDTO;
import com.example.Uniride.Model.Notificacion;
import java.util.List;


public interface NotificacionService {
    List<Notificacion> listarTodas();
    Notificacion guardar(NotificacionDTO dto);
    void eliminar(Long id);
    List<Notificacion> buscarPorDestinatarios(String destinatarios);
    List<Notificacion> buscarPorUsuario(Long idUsuario);
    void marcarLeida(Long id);
}