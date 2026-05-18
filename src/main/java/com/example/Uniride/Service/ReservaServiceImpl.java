package com.example.Uniride.Service;

import com.example.Uniride.DTO.ReservaDTO;
import com.example.Uniride.Model.Reserva;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Model.Viaje;
import com.example.Uniride.Repository.ReservaRepository;
import com.example.Uniride.Repository.UsuarioRepository;
import com.example.Uniride.Repository.ViajeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ViajeRepository viajeRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              UsuarioRepository usuarioRepository,
                              ViajeRepository viajeRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.viajeRepository = viajeRepository;
    }

    private Reserva toEntity(ReservaDTO dto) {
        Reserva r = new Reserva();
        r.setFechaReserva(dto.getFechaReserva());
        r.setConfirmada(dto.getConfirmada() != null ? dto.getConfirmada() : false);
        Usuario u = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + dto.getIdUsuario()));
        r.setUsuario(u);
        Viaje v = viajeRepository.findById(dto.getIdViaje())
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado con id: " + dto.getIdViaje()));
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
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
    }

    @Override
    public Reserva guardar(ReservaDTO dto) {
        return reservaRepository.save(toEntity(dto));
    }

    @Override
    public Reserva actualizar(Long id, ReservaDTO dto) {
        Reserva r = buscarPorId(id);
        r.setFechaReserva(dto.getFechaReserva());
        r.setConfirmada(dto.getConfirmada());
        if (dto.getIdUsuario() != null) {
            Usuario u = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + dto.getIdUsuario()));
            r.setUsuario(u);
        }
        if (dto.getIdViaje() != null) {
            Viaje v = viajeRepository.findById(dto.getIdViaje())
                    .orElseThrow(() -> new RuntimeException("Viaje no encontrado con id: " + dto.getIdViaje()));
            r.setViaje(v);
        }
        return reservaRepository.save(r);
    }

    @Override
    public void eliminar(Long id) {
        if (!reservaRepository.existsById(id))
            throw new RuntimeException("Reserva no encontrada con id: " + id);
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
}