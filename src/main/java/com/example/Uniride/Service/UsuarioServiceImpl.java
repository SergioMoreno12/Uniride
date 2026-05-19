package com.example.Uniride.Service;

import com.example.Uniride.DTO.*;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Usuario toEntity(UsuarioDTO dto) {
        Usuario u = new Usuario();
        u.setNombre(dto.getNombre());
        u.setCorreo(dto.getCorreo());
        u.setTelefono(dto.getTelefono());
        u.setFechaRegistro(dto.getFechaRegistro());
        u.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        u.setRol(dto.getRol() != null ? dto.getRol() : "pasajero");
        u.setActivo(true);
        return u;
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
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
        if (dto.getRol() != null) u.setRol(dto.getRol());
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank())
            u.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        return usuarioRepository.save(u);
    }

    @Override
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id))
            throw new RuntimeException("Usuario no encontrado: " + id);
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + correo));
    }

    @Override
    public Usuario actualizarPerfil(Long id, ActualizarPerfilDTO dto) {
        Usuario u = buscarPorId(id);
        if (dto.getNombre() != null && !dto.getNombre().isBlank())
            u.setNombre(dto.getNombre());
        if (dto.getTelefono() != null)
            u.setTelefono(dto.getTelefono());
        return usuarioRepository.save(u);
    }

    @Override
    public void cambiarContrasena(Long id, CambiarContrasenaDTO dto) {
        Usuario u = buscarPorId(id);
        if (!passwordEncoder.matches(dto.getContrasenaActual(), u.getContrasena()))
            throw new RuntimeException("La contraseña actual es incorrecta.");
        u.setContrasena(passwordEncoder.encode(dto.getContrasenaNueva()));
        usuarioRepository.save(u);
    }

    @Override
    public Usuario toggleActivo(Long id) {
        Usuario u = buscarPorId(id);
        u.setActivo(!u.getActivo());
        return usuarioRepository.save(u);
    }
}