package com.example.Uniride.Service;

import com.example.Uniride.DTO.ReservaDTO;
import com.example.Uniride.Model.Notificacion;
import com.example.Uniride.Model.Reserva;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Model.Viaje;
import com.example.Uniride.Repository.NotificacionRepository;
import com.example.Uniride.Repository.ReservaRepository;
import com.example.Uniride.Repository.UsuarioRepository;
import com.example.Uniride.Repository.ViajeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository      reservaRepository;
    private final UsuarioRepository      usuarioRepository;
    private final ViajeRepository        viajeRepository;
    private final NotificacionRepository notificacionRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              UsuarioRepository usuarioRepository,
                              ViajeRepository viajeRepository,
                              NotificacionRepository notificacionRepository) {
        this.reservaRepository      = reservaRepository;
        this.usuarioRepository      = usuarioRepository;
        this.viajeRepository        = viajeRepository;
        this.notificacionRepository = notificacionRepository;
    }

    private Reserva toEntity(ReservaDTO dto) {
        Reserva r = new Reserva();
        r.setFechaReserva(dto.getFechaReserva());
        r.setConfirmada(dto.getConfirmada() != null ? dto.getConfirmada() : false);
        Usuario u = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + dto.getIdUsuario()));
        r.setUsuario(u);
        Viaje v = viajeRepository.findById(dto.getIdViaje())
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado: " + dto.getIdViaje()));
        r.setViaje(v);
        return r;
    }

    @Override
    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    @Override
    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + id));
    }

    @Override
    public Reserva guardar(ReservaDTO dto) {
        Viaje viaje = viajeRepository.findById(dto.getIdViaje())
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado: " + dto.getIdViaje()));

        if (!viaje.getEstado().equals("disponible"))
            throw new RuntimeException("El viaje no está disponible para reservas.");

        int yaReservo = reservaRepository.existeReservaPorUsuarioYViaje(
                dto.getIdViaje(), dto.getIdUsuario());
        if (yaReservo > 0)
            throw new RuntimeException("El usuario ya tiene una reserva en este viaje.");

        int capacidad = viaje.getVehiculo().getCapacidad();
        int reservasActuales = reservaRepository.contarReservasConfirmadasPorViaje(dto.getIdViaje());

        if (reservasActuales >= capacidad)
            throw new RuntimeException("El viaje ya no tiene puestos disponibles.");

        Reserva nuevaReserva = reservaRepository.save(toEntity(dto));

        if (reservasActuales + 1 >= capacidad) {
            viaje.setEstado("lleno");
            viajeRepository.save(viaje);
        }
        return nuevaReserva;
    }

    @Override
    public Reserva actualizar(Long id, ReservaDTO dto) {
        Reserva r = buscarPorId(id);
        r.setFechaReserva(dto.getFechaReserva());
        r.setConfirmada(dto.getConfirmada());
        if (dto.getIdUsuario() != null) {
            Usuario u = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + dto.getIdUsuario()));
            r.setUsuario(u);
        }
        if (dto.getIdViaje() != null) {
            Viaje v = viajeRepository.findById(dto.getIdViaje())
                    .orElseThrow(() -> new RuntimeException("Viaje no encontrado: " + dto.getIdViaje()));
            r.setViaje(v);
        }
        return reservaRepository.save(r);
    }

    @Override
    public void eliminar(Long id) {
        if (!reservaRepository.existsById(id))
            throw new RuntimeException("Reserva no encontrada: " + id);
        reservaRepository.deleteById(id);
    }

    @Override
    public List<Reserva> buscarPorUsuario(Long idUsuario) {
        return reservaRepository.findByIdUsuario(idUsuario);
    }

    @Override
    public List<Reserva> buscarPorViaje(Long idViaje) {
        return reservaRepository.findByIdViaje(idViaje);
    }

    @Override
    public List<Reserva> confirmadasPorViaje(Long idViaje) {
        return reservaRepository.findConfirmadasByViaje(idViaje);
    }

    @Override
    public Reserva confirmar(Long id) {
        Reserva r = buscarPorId(id);
        r.setConfirmada(true);
        Reserva confirmada = reservaRepository.save(r);

        // Enviar notificación al pasajero
        try {
            Notificacion notif = new Notificacion();
            notif.setTitulo("¡Reserva confirmada! 🎉");
            notif.setMensaje(
                    "Tu reserva para el viaje de " +
                            r.getViaje().getOrigen() + " a " +
                            r.getViaje().getSede().getNombreSede() +
                            " el " + r.getViaje().getFechaHora().toLocalDate() +
                            " ha sido confirmada. Punto de encuentro: " +
                            (r.getViaje().getDescripcionPunto() != null
                                    ? r.getViaje().getDescripcionPunto()
                                    : "Por confirmar por el conductor.")
            );
            notif.setDestinatarios("pasajero");
            notif.setIdUsuario(r.getUsuario().getIdUsuario());
            notif.setLeida(false);
            notif.setFechaEnvio(LocalDateTime.now());
            notificacionRepository.save(notif);
        } catch (Exception e) {
            // No bloquear la confirmación si falla la notificación
            System.err.println("Error al enviar notificación: " + e.getMessage());
        }

        return confirmada;
    }

    @Override
    public void cancelar(Long id) {
        Reserva r = buscarPorId(id);
        Viaje viaje = r.getViaje();
        if (viaje.getEstado().equals("lleno")) {
            viaje.setEstado("disponible");
            viajeRepository.save(viaje);
        }
        reservaRepository.deleteById(id);
    }
}