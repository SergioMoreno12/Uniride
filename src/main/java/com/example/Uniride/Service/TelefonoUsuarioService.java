package com.example.Uniride.Service;

import com.example.Uniride.DTO.TelefonoUsuarioDTO;
import com.example.Uniride.Model.TelefonoUsuario;
import java.util.List;

public interface TelefonoUsuarioService {
    List<TelefonoUsuario> listarTodos();
    TelefonoUsuario buscarPorId(Long id);
    TelefonoUsuario guardar(TelefonoUsuarioDTO dto);
    TelefonoUsuario actualizar(Long id, TelefonoUsuarioDTO dto);
    void eliminar(Long id);
    List<TelefonoUsuario> buscarPorUsuario(Long idUsuario);
}