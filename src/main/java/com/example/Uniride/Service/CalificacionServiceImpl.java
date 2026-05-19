package com.example.Uniride.Service;

import com.example.Uniride.DTO.CalificacionDTO;
import com.example.Uniride.Model.Calificacion;
import com.example.Uniride.Model.Reserva;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.CalificacionRepository;
import com.example.Uniride.Repository.ReservaRepository;
import com.example.Uniride.Repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    public CalificacionServiceImpl(CalificacionRepository calificacionRepository,
                                   ReservaRepository reservaRepository,
                                   UsuarioRepository usuarioRepository) {
        this.calificacionRepository = calificacionRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Calificacion guardar(CalificacionDTO dto) {
        if (calificacionRepository.findByIdReserva(dto.getIdReserva()).isPresent())
            throw new RuntimeException("Ya calificaste este viaje.");

        Reserva reserva = reservaRepository.findById(dto.getIdReserva())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada."));
        Usuario conductor = usuarioRepository.findById(dto.getIdConductor())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado."));
        Usuario pasajero = usuarioRepository.findById(dto.getIdPasajero())
                .orElseThrow(() -> new RuntimeException("Pasajero no encontrado."));

        Calificacion c = new Calificacion();
        c.setPuntuacion(dto.getPuntuacion());
        c.setComentario(dto.getComentario());
        c.setFechaCalificacion(dto.getFechaCalificacion());
        c.setReserva(reserva);
        c.setConductor(conductor);
        c.setPasajero(pasajero);
        return calificacionRepository.save(c);
    }

    @Override
    public List<Calificacion> buscarPorConductor(Long idConductor) {
        return calificacionRepository.findByIdConductor(idConductor);
    }

    @Override
    public List<Calificacion> buscarPorPasajero(Long idPasajero) {
        return calificacionRepository.findByIdPasajero(idPasajero);
    }

    @Override
    public Double promedioConductor(Long idConductor) {
        Double promedio = calificacionRepository.promedioCalificacionConductor(idConductor);
        return promedio != null ? promedio : 0.0;
    }

    @Override
    public boolean yaCalificada(Long idReserva) {
        return calificacionRepository.findByIdReserva(idReserva).isPresent();
    }
}