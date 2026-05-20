package com.example.Uniride.Service;

import com.example.Uniride.DTO.ActualizarPerfilDTO;
import com.example.Uniride.DTO.CambiarContrasenaDTO;
import com.example.Uniride.DTO.UsuarioDTO;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Usuario> listarTodos() { return usuarioRepository.findAll(); }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    }

    @Override
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + correo));
    }

    @Override
    public Usuario guardar(UsuarioDTO dto) {
        if (usuarioRepository.findByCorreo(dto.getCorreo()).isPresent())
            throw new RuntimeException("El correo ya está registrado");
        Usuario u = new Usuario();
        u.setCorreo(dto.getCorreo());
        u.setNombre(dto.getNombre());
        u.setTelefono(dto.getTelefono());
        u.setContrasena(encoder.encode(dto.getContrasena()));
        u.setRol(dto.getRol() != null ? dto.getRol() : "pasajero");
        u.setFechaRegistro(LocalDate.now());
        u.setActivo(true);
        return usuarioRepository.save(u);
    }

    @Override
    public Usuario actualizarPerfil(Long id, ActualizarPerfilDTO dto) {
        Usuario u = buscarPorId(id);
        if (dto.getNombre()    != null) u.setNombre(dto.getNombre());
        if (dto.getTelefono()  != null) u.setTelefono(dto.getTelefono());
        if (dto.getRol()       != null) u.setRol(dto.getRol());
        if (dto.getFotoPerfil()!= null) u.setFotoPerfil(dto.getFotoPerfil());
        return usuarioRepository.save(u);
    }

    @Override
    public void cambiarContrasena(Long id, CambiarContrasenaDTO dto) {
        Usuario u = buscarPorId(id);
        if (!encoder.matches(dto.getContrasenaActual(), u.getContrasena()))
            throw new RuntimeException("Contraseña actual incorrecta");
        u.setContrasena(encoder.encode(dto.getContrasenaNueva()));
        usuarioRepository.save(u);
    }

    @Override
    public Usuario toggleActivo(Long id) {
        Usuario u = buscarPorId(id);
        u.setActivo(!u.getActivo());
        return usuarioRepository.save(u);
    }

    @Override
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id))
            throw new RuntimeException("Usuario no encontrado: " + id);
        usuarioRepository.deleteById(id);
    }
}