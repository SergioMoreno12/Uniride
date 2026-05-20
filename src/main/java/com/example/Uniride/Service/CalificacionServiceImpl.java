package com.example.Uniride.Service;

import com.example.Uniride.DTO.CalificacionDTO;
import com.example.Uniride.Model.Calificacion;
import com.example.Uniride.Model.Notificacion;
import com.example.Uniride.Model.Reserva;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.CalificacionRepository;
import com.example.Uniride.Repository.NotificacionRepository;
import com.example.Uniride.Repository.ReservaRepository;
import com.example.Uniride.Repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final ReservaRepository      reservaRepository;
    private final UsuarioRepository      usuarioRepository;
    private final NotificacionRepository notificacionRepository;

    public CalificacionServiceImpl(
            CalificacionRepository calificacionRepository,
            ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            NotificacionRepository notificacionRepository) {
        this.calificacionRepository = calificacionRepository;
        this.reservaRepository      = reservaRepository;
        this.usuarioRepository      = usuarioRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @Override
    public Calificacion guardar(CalificacionDTO dto) {
        // Verificar que no haya calificado antes
        if (calificacionRepository.existsByIdReserva(dto.getIdReserva()) > 0)
            throw new RuntimeException("Ya calificaste este viaje.");

        Reserva reserva = reservaRepository.findById(dto.getIdReserva())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        Usuario conductor = usuarioRepository.findById(dto.getIdConductor())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));
        Usuario pasajero  = usuarioRepository.findById(dto.getIdPasajero())
                .orElseThrow(() -> new RuntimeException("Pasajero no encontrado"));

        Calificacion c = new Calificacion();
        c.setPuntuacion(dto.getPuntuacion());
        c.setComentario(dto.getComentario());
        c.setFechaCalificacion(LocalDate.now());
        c.setReserva(reserva);
        c.setConductor(conductor);
        c.setPasajero(pasajero);
        Calificacion saved = calificacionRepository.save(c);

        // Marcar reserva como calificada
        try {
            reserva.setCalificada(true);
            reservaRepository.save(reserva);
        } catch (Exception ignored) { }

        // Notificar al conductor sobre la calificación
        try {
            Notificacion notif = new Notificacion();
            notif.setTitulo("Nueva calificación ⭐");
            notif.setMensaje(pasajero.getNombre() + " te calificó con " +
                    dto.getPuntuacion() + " estrella" +
                    (dto.getPuntuacion() > 1 ? "s" : "") +
                    (dto.getComentario() != null && !dto.getComentario().isBlank()
                            ? ": \"" + dto.getComentario() + "\"" : ""));
            notif.setDestinatarios("conductor");
            notif.setIdUsuario(conductor.getIdUsuario());
            notif.setLeida(false);
            notif.setFechaEnvio(LocalDateTime.now());
            notificacionRepository.save(notif);
        } catch (Exception ignored) { }

        return saved;
    }

    @Override
    public List<Calificacion> buscarPorConductor(Long idConductor) {
        return calificacionRepository.findByIdConductor(idConductor);
    }

    @Override
    public Double promedioConductor(Long idConductor) {
        Double p = calificacionRepository.promedioPorConductor(idConductor);
        return p != null ? p : 0.0;
    }

    @Override
    public boolean yaCalificada(Long idReserva) {
        return calificacionRepository.existsByIdReserva(idReserva) > 0;
    }
}