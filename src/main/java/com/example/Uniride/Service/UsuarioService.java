package com.example.Uniride.Service;

import com.example.Uniride.DTO.ActualizarPerfilDTO;
import com.example.Uniride.DTO.CambiarContrasenaDTO;
import com.example.Uniride.DTO.UsuarioDTO;
import com.example.Uniride.Model.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> listarTodos();
    Usuario buscarPorId(Long id);
    Usuario buscarPorCorreo(String correo);
    Usuario guardar(UsuarioDTO dto);
    Usuario actualizarPerfil(Long id, ActualizarPerfilDTO dto);
    void cambiarContrasena(Long id, CambiarContrasenaDTO dto);
    Usuario toggleActivo(Long id);
    void eliminar(Long id);
}