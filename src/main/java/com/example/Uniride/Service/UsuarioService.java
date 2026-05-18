package com.example.Uniride.Service;

import com.example.Uniride.DTO.UsuarioDTO;
import com.example.Uniride.Model.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> listarTodos();
    Usuario buscarPorId(Long id);
    Usuario guardar(UsuarioDTO dto);
    Usuario actualizar(Long id, UsuarioDTO dto);
    void eliminar(Long id);
    Usuario buscarPorCorreo(String correo);
}