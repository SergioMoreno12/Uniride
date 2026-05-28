package com.example.Uniride.Service;

import com.example.Uniride.DTO.ReservaDTO;
import com.example.Uniride.Model.*;
import com.example.Uniride.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

    private static final Logger logger = LoggerFactory.getLogger(ReservaServiceImpl.class);

    private final ReservaRepository      reservaRepository;
    private final UsuarioRepository      usuarioRepository;
    private final ViajeRepository        viajeRepository;
    private final NotificacionRepository notificacionRepository;

    public ReservaServiceImpl(
            ReservaRepository reservaRepository,
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
        r.setFechaReserva(dto.getFechaReserva() != null
                ? dto.getFechaReserva() : LocalDate.now());
        r.setConfirmada(dto.getConfirmada() != null ? dto.getConfirmada() : false);
        r.setCalificada(false);
        Usuario u = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        r.setUsuario(u);
        Viaje v = viajeRepository.findById(dto.getIdViaje())
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));
        r.setViaje(v);
        return r;
    }

    @Override
    public List<Reserva> listarTodas() { return reservaRepository.findAll(); }

    @Override
    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + id));
    }

    @Override
    public Reserva guardar(ReservaDTO dto) {
        Viaje viaje = viajeRepository.findById(dto.getIdViaje())
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));

        if (!viaje.getEstado().equals("disponible"))
            throw new RuntimeException("El viaje no está disponible para reservas.");

        // Bloquear si el usuario es dueño del vehículo
        if (viaje.getVehiculo() != null && viaje.getVehiculo().getUsuario() != null) {
            Long idDuenio = viaje.getVehiculo().getUsuario().getIdUsuario();
            if (idDuenio.equals(dto.getIdUsuario()))
                throw new RuntimeException("No puedes reservar en un viaje que tú publicaste.");
        }

        int yaReservo = reservaRepository.existeReservaPorUsuarioYViaje(
                dto.getIdViaje(), dto.getIdUsuario());
        if (yaReservo > 0)
            throw new RuntimeException("Ya tienes una reserva en este viaje.");

        int capacidad        = viaje.getVehiculo() != null ? viaje.getVehiculo().getCapacidad() : 0;
        int reservasActuales = reservaRepository.contarReservasConfirmadasPorViaje(dto.getIdViaje());

        if (reservasActuales >= capacidad)
            throw new RuntimeException("El viaje ya no tiene puestos disponibles.");

        Reserva nueva = reservaRepository.save(toEntity(dto));
        logger.info("Reserva creada id={} para usuario={} en viaje={}",
                nueva.getIdReserva(), dto.getIdUsuario(), dto.getIdViaje());

        if (reservasActuales + 1 >= capacidad) {
            viaje.setEstado("lleno");
            viajeRepository.save(viaje);
            logger.info("Viaje id={} marcado como lleno", viaje.getIdViaje());
        }

        // Notificar al conductor con idReserva para navegación directa
        try {
            Long idConductor = viaje.getVehiculo().getUsuario().getIdUsuario();
            Usuario pasajero = usuarioRepository.findById(dto.getIdUsuario()).orElse(null);
            Notificacion notif = new Notificacion();
            notif.setTitulo("Nueva reserva en tu viaje 🚗");
            notif.setMensaje((pasajero != null ? pasajero.getNombre() : "Un pasajero") +
                    " ha reservado un puesto en tu viaje de " +
                    viaje.getOrigen() + " a " +
                    (viaje.getSede() != null ? viaje.getSede().getNombreSede() : "la sede") +
                    " el " + viaje.getFechaHora().toLocalDate() +
                    ". Confírmale la reserva.");
            notif.setDestinatarios("conductor");
            notif.setIdUsuario(idConductor);
            notif.setIdViaje(dto.getIdViaje());
            notif.setIdReserva(nueva.getIdReserva()); // ← permite ir directo a confirmar
            notif.setLeida(false);
            notif.setFechaEnvio(LocalDateTime.now());
            notificacionRepository.save(notif);
        } catch (Exception e) {
            logger.warn("Error al crear notificación de reserva: {}", e.getMessage());
        }

        return nueva;
    }

    @Override
    public Reserva actualizar(Long id, ReservaDTO dto) {
        Reserva r = buscarPorId(id);
        if (dto.getFechaReserva() != null) r.setFechaReserva(dto.getFechaReserva());
        if (dto.getConfirmada()   != null) r.setConfirmada(dto.getConfirmada());
        return reservaRepository.save(r);
    }

    @Override
    public void eliminar(Long id) {
        if (!reservaRepository.existsById(id))
            throw new RuntimeException("Reserva no encontrada: " + id);
        reservaRepository.deleteById(id);
        logger.info("Reserva id={} eliminada", id);
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
        logger.info("Reserva id={} confirmada", id);

        // Notificar al pasajero con idReserva para ver detalle directamente
        try {
            Notificacion notif = new Notificacion();
            notif.setTitulo("¡Reserva confirmada! 🎉");
            String origen = r.getViaje() != null ? r.getViaje().getOrigen() : "";
            String sede   = (r.getViaje() != null && r.getViaje().getSede() != null)
                    ? r.getViaje().getSede().getNombreSede() : "la sede";
            String fecha  = r.getViaje() != null
                    ? r.getViaje().getFechaHora().toLocalDate().toString() : "";
            String punto  = (r.getViaje() != null && r.getViaje().getDescripcionPunto() != null)
                    ? r.getViaje().getDescripcionPunto() : "Por confirmar";

            notif.setMensaje("Tu reserva para el viaje de " + origen + " a " + sede +
                    " el " + fecha + " ha sido confirmada. Punto de encuentro: " + punto);
            notif.setDestinatarios("pasajero");
            notif.setIdUsuario(r.getUsuario().getIdUsuario());
            notif.setIdViaje(r.getViaje().getIdViaje());
            notif.setIdReserva(r.getIdReserva()); // ← permite ir directo al detalle
            notif.setLeida(false);
            notif.setFechaEnvio(LocalDateTime.now());
            notificacionRepository.save(notif);
        } catch (Exception e) {
            logger.warn("Error al crear notificación de confirmación: {}", e.getMessage());
        }

        return confirmada;
    }

    @Override
    public void cancelar(Long id) {
        Reserva r = buscarPorId(id);
        Viaje viaje = r.getViaje();
        if (viaje != null && viaje.getEstado().equals("lleno")) {
            viaje.setEstado("disponible");
            viajeRepository.save(viaje);
            logger.info("Viaje id={} vuelto a disponible por cancelación de reserva",
                    viaje.getIdViaje());
        }
        reservaRepository.deleteById(id);
        logger.info("Reserva id={} cancelada", id);
    }

    @Override
    public void marcarCalificada(Long id) {
        Reserva r = buscarPorId(id);
        r.setCalificada(true);
        reservaRepository.save(r);
    }
}