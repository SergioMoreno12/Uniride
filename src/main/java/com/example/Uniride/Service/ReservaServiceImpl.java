package com.example.Uniride.Service;

import com.example.Uniride.DTO.ReservaDTO;
import com.example.Uniride.Model.*;
import com.example.Uniride.Repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

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

        // Bloquear si el usuario es dueño del vehículo (aunque haya cambiado de rol)
        Long idDuenio = viaje.getVehiculo().getUsuario().getIdUsuario();
        if (idDuenio.equals(dto.getIdUsuario()))
            throw new RuntimeException("No puedes reservar en un viaje que tú publicaste.");

        int yaReservo = reservaRepository.existeReservaPorUsuarioYViaje(
                dto.getIdViaje(), dto.getIdUsuario());
        if (yaReservo > 0)
            throw new RuntimeException("Ya tienes una reserva en este viaje.");

        int capacidad        = viaje.getVehiculo().getCapacidad();
        int reservasActuales = reservaRepository.contarReservasConfirmadasPorViaje(dto.getIdViaje());

        if (reservasActuales >= capacidad)
            throw new RuntimeException("El viaje ya no tiene puestos disponibles.");

        Reserva nueva = reservaRepository.save(toEntity(dto));

        if (reservasActuales + 1 >= capacidad) {
            viaje.setEstado("lleno");
            viajeRepository.save(viaje);
        }

        // Notificar al conductor sobre la nueva reserva
        try {
            Usuario pasajero   = usuarioRepository.findById(dto.getIdUsuario()).orElse(null);
            Long    idConductor = idDuenio;
            Notificacion notif = new Notificacion();
            notif.setTitulo("Nueva reserva en tu viaje 🚗");
            notif.setMensaje((pasajero != null ? pasajero.getNombre() : "Un pasajero") +
                    " ha reservado un puesto en tu viaje de " +
                    viaje.getOrigen() + " a " +
                    viaje.getSede().getNombreSede() +
                    " el " + viaje.getFechaHora().toLocalDate() +
                    ". Confírmale la reserva.");
            notif.setDestinatarios("conductor");
            notif.setIdUsuario(idConductor);
            notif.setIdViaje(dto.getIdViaje());
            notif.setLeida(false);
            notif.setFechaEnvio(LocalDateTime.now());
            notificacionRepository.save(notif);
        } catch (Exception ignored) { }

        return nueva;
    }

    @Override
    public Reserva actualizar(Long id, ReservaDTO dto) {
        Reserva r = buscarPorId(id);
        r.setFechaReserva(dto.getFechaReserva());
        r.setConfirmada(dto.getConfirmada());
        return reservaRepository.save(r);
    }

    @Override
    public void eliminar(Long id) {
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

        // Notificar al pasajero que su reserva fue confirmada
        try {
            Notificacion notif = new Notificacion();
            notif.setTitulo("¡Reserva confirmada! 🎉");
            notif.setMensaje("Tu reserva para el viaje de " +
                    r.getViaje().getOrigen() + " a " +
                    r.getViaje().getSede().getNombreSede() +
                    " el " + r.getViaje().getFechaHora().toLocalDate() +
                    " ha sido confirmada. Punto de encuentro: " +
                    (r.getViaje().getDescripcionPunto() != null
                            ? r.getViaje().getDescripcionPunto() : "Por confirmar"));
            notif.setDestinatarios("pasajero");
            notif.setIdUsuario(r.getUsuario().getIdUsuario());
            notif.setIdViaje(r.getViaje().getIdViaje());
            notif.setLeida(false);
            notif.setFechaEnvio(LocalDateTime.now());
            notificacionRepository.save(notif);
        } catch (Exception ignored) { }

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

    @Override
    public void marcarCalificada(Long id) {
        Reserva r = buscarPorId(id);
        r.setCalificada(true);
        reservaRepository.save(r);
    }
}