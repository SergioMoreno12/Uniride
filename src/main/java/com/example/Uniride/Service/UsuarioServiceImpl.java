package com.example.Uniride.Service;

import com.example.Uniride.DTO.ActualizarPerfilDTO;
import com.example.Uniride.DTO.CambiarContrasenaDTO;
import com.example.Uniride.DTO.UsuarioDTO;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository         usuarioRepository;
    private final ReservaRepository         reservaRepository;
    private final VehiculoRepository        vehiculoRepository;
    private final ViajeRepository           viajeRepository;
    private final CalificacionRepository    calificacionRepository;
    private final ReporteRepository         reporteRepository;
    private final TelefonoUsuarioRepository telefonoRepository;
    private final NotificacionRepository    notificacionRepository;
    private final PasswordEncoder           encoder;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            ReservaRepository reservaRepository,
            VehiculoRepository vehiculoRepository,
            ViajeRepository viajeRepository,
            CalificacionRepository calificacionRepository,
            ReporteRepository reporteRepository,
            TelefonoUsuarioRepository telefonoRepository,
            NotificacionRepository notificacionRepository,
            PasswordEncoder encoder) {
        this.usuarioRepository      = usuarioRepository;
        this.reservaRepository      = reservaRepository;
        this.vehiculoRepository     = vehiculoRepository;
        this.viajeRepository        = viajeRepository;
        this.calificacionRepository = calificacionRepository;
        this.reporteRepository      = reporteRepository;
        this.telefonoRepository     = telefonoRepository;
        this.notificacionRepository = notificacionRepository;
        this.encoder                = encoder;
    }

    @Override
    public List<Usuario> listarTodos() {
        logger.debug("Listando todos los usuarios");
        return usuarioRepository.findAll();
    }

    @Override
    public Page<Usuario> listarPaginado(Pageable pageable) {
        logger.debug("Listando usuarios paginados - página {}", pageable.getPageNumber());
        return usuarioRepository.findAll(pageable);
    }

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
        if (dto.getCorreo() == null || dto.getCorreo().isBlank())
            throw new RuntimeException("El correo es obligatorio");
        if (dto.getContrasena() == null || dto.getContrasena().length() < 6)
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new RuntimeException("El nombre es obligatorio");

        String correoNorm = dto.getCorreo().trim().toLowerCase();
        if (usuarioRepository.findByCorreo(correoNorm).isPresent())
            throw new RuntimeException("El correo ya está registrado");

        String rol = dto.getRol() != null ? dto.getRol() : "pasajero";
        if ("administrador".equals(rol))
            throw new RuntimeException("No se puede registrar un usuario con rol administrador.");

        Usuario u = new Usuario();
        u.setCorreo(correoNorm);
        u.setNombre(dto.getNombre().trim());
        u.setTelefono(dto.getTelefono());
        u.setContrasena(encoder.encode(dto.getContrasena()));
        u.setRol(rol);
        u.setFechaRegistro(LocalDate.now());
        u.setActivo(true);

        logger.info("Nuevo usuario registrado: correo={}, rol={}", correoNorm, rol);
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
        logger.info("Perfil actualizado para usuario id={}", id);
        return usuarioRepository.save(u);
    }

    @Override
    public void cambiarContrasena(Long id, CambiarContrasenaDTO dto) {
        Usuario u = buscarPorId(id);
        if (dto.getContrasenaNueva() == null || dto.getContrasenaNueva().length() < 6)
            throw new RuntimeException("La nueva contraseña debe tener al menos 6 caracteres");
        if (!encoder.matches(dto.getContrasenaActual(), u.getContrasena()))
            throw new RuntimeException("Contraseña actual incorrecta");
        u.setContrasena(encoder.encode(dto.getContrasenaNueva()));
        usuarioRepository.save(u);
        logger.info("Contraseña cambiada para usuario id={}", id);
    }

    @Override
    public Usuario toggleActivo(Long id) {
        Usuario u = buscarPorId(id);
        u.setActivo(!u.getActivo());
        logger.info("Estado activo cambiado para usuario id={}: activo={}", id, u.getActivo());
        return usuarioRepository.save(u);
    }

    /** Eliminación en cascada manual */
    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id))
            throw new RuntimeException("Usuario no encontrado: " + id);

        calificacionRepository.deleteByIdPasajero(id);
        calificacionRepository.deleteByIdConductor(id);
        reservaRepository.deleteByIdUsuario(id);

        List<Long> idsVehiculos = vehiculoRepository.findIdsByIdUsuario(id);
        for (Long idVehiculo : idsVehiculos) {
            List<Long> idsViajes = viajeRepository.findIdsByIdVehiculo(idVehiculo);
            for (Long idViaje : idsViajes) {
                calificacionRepository.deleteByIdReservaViaje(idViaje);
                reservaRepository.deleteByIdViaje(idViaje);
                notificacionRepository.deleteByIdViaje(idViaje);
            }
            viajeRepository.deleteByIdVehiculo(idVehiculo);
        }

        vehiculoRepository.deleteByIdUsuario(id);
        reporteRepository.deleteByIdUsuario(id);
        telefonoRepository.deleteByIdUsuario(id);
        notificacionRepository.deleteByIdUsuario(id);
        usuarioRepository.deleteById(id);

        logger.info("Usuario id={} eliminado correctamente con todos sus datos", id);
    }

    @Override
    public List<Usuario> listarConductores() {
        return usuarioRepository.findByRol("conductor");
    }
}