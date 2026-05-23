package com.example.Uniride.Service;

import com.example.Uniride.DTO.ActualizarPerfilDTO;
import com.example.Uniride.DTO.CambiarContrasenaDTO;
import com.example.Uniride.DTO.UsuarioDTO;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository      usuarioRepository;
    private final ReservaRepository      reservaRepository;
    private final VehiculoRepository     vehiculoRepository;
    private final ViajeRepository        viajeRepository;
    private final CalificacionRepository calificacionRepository;
    private final ReporteRepository      reporteRepository;
    private final TelefonoUsuarioRepository telefonoRepository;
    private final NotificacionRepository notificacionRepository;
    private final BCryptPasswordEncoder  encoder = new BCryptPasswordEncoder();

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            ReservaRepository reservaRepository,
            VehiculoRepository vehiculoRepository,
            ViajeRepository viajeRepository,
            CalificacionRepository calificacionRepository,
            ReporteRepository reporteRepository,
            TelefonoUsuarioRepository telefonoRepository,
            NotificacionRepository notificacionRepository) {
        this.usuarioRepository      = usuarioRepository;
        this.reservaRepository      = reservaRepository;
        this.vehiculoRepository     = vehiculoRepository;
        this.viajeRepository        = viajeRepository;
        this.calificacionRepository = calificacionRepository;
        this.reporteRepository      = reporteRepository;
        this.telefonoRepository     = telefonoRepository;
        this.notificacionRepository = notificacionRepository;
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

        String rol = dto.getRol() != null ? dto.getRol() : "pasajero";
        if ("administrador".equals(rol))
            throw new RuntimeException("No se puede registrar un usuario con rol administrador.");

        Usuario u = new Usuario();
        u.setCorreo(dto.getCorreo());
        u.setNombre(dto.getNombre());
        u.setTelefono(dto.getTelefono());
        u.setContrasena(encoder.encode(dto.getContrasena()));
        u.setRol(rol);
        u.setFechaRegistro(LocalDate.now());
        u.setActivo(true);
        return usuarioRepository.save(u);
    }

    @Override
    public Usuario actualizarPerfil(Long id, ActualizarPerfilDTO dto) {
        Usuario u = buscarPorId(id);
        if (dto.getNombre()     != null) u.setNombre(dto.getNombre());
        if (dto.getTelefono()   != null) u.setTelefono(dto.getTelefono());
        if (dto.getFotoPerfil() != null) u.setFotoPerfil(dto.getFotoPerfil());
        if (dto.getRol()        != null) {
            if ("administrador".equals(dto.getRol()))
                throw new RuntimeException("No se puede asignar el rol administrador.");
            u.setRol(dto.getRol());
        }
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

    /**
     * Eliminación en cascada manual:
     * calificaciones → reservas → (calificaciones de viajes del usuario →
     * reservas de viajes → viajes) → vehículos → reportes → teléfonos → usuario
     */
    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id))
            throw new RuntimeException("Usuario no encontrado: " + id);

        // 1. Eliminar calificaciones donde el usuario es pasajero o conductor
        calificacionRepository.deleteByIdPasajero(id);
        calificacionRepository.deleteByIdConductor(id);

        // 2. Eliminar reservas del usuario como pasajero
        reservaRepository.deleteByIdUsuario(id);

        // 3. Eliminar datos de viajes que publicó como conductor
        List<Long> idsVehiculos = vehiculoRepository.findIdsByIdUsuario(id);
        for (Long idVehiculo : idsVehiculos) {
            List<Long> idsViajes = viajeRepository.findIdsByIdVehiculo(idVehiculo);
            for (Long idViaje : idsViajes) {
                // Calificaciones de ese viaje
                calificacionRepository.deleteByIdReservaViaje(idViaje);
                // Reservas de ese viaje
                reservaRepository.deleteByIdViaje(idViaje);
            }
            // Viajes del vehículo
            viajeRepository.deleteByIdVehiculo(idVehiculo);
        }

        // 4. Eliminar vehículos
        vehiculoRepository.deleteByIdUsuario(id);

        // 5. Eliminar reportes
        reporteRepository.deleteByIdUsuario(id);

        // 6. Eliminar teléfonos
        telefonoRepository.deleteByIdUsuario(id);

        // 7. Eliminar notificaciones
        notificacionRepository.deleteByIdUsuario(id);

        // 8. Eliminar usuario
        usuarioRepository.deleteById(id);
    }
}