package com.example.Uniride.Service;

import com.example.Uniride.DTO.UsuarioDTO;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario toEntity(UsuarioDTO dto) {
        Usuario u = new Usuario();
        u.setNombre(dto.getNombre());
        u.setCorreo(dto.getCorreo());
        u.setTelefono(dto.getTelefono());
        u.setFechaRegistro(dto.getFechaRegistro());
        return u;
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    @Override
    public Usuario guardar(UsuarioDTO dto) {
        return usuarioRepository.save(toEntity(dto));
    }

    @Override
    public Usuario actualizar(Long id, UsuarioDTO dto) {
        Usuario u = buscarPorId(id);
        u.setNombre(dto.getNombre());
        u.setCorreo(dto.getCorreo());
        u.setTelefono(dto.getTelefono());
        u.setFechaRegistro(dto.getFechaRegistro());
        return usuarioRepository.save(u);
    }

    @Override
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id))
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
    }
}