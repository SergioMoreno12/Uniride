package com.example.Uniride.Service;

import com.example.Uniride.DTO.VehiculoDTO;
import com.example.Uniride.Model.*;
import com.example.Uniride.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository     vehiculoRepository;
    private final UsuarioRepository      usuarioRepository;
    private final ViajeRepository        viajeRepository;
    private final ReservaRepository      reservaRepository;
    private final CalificacionRepository calificacionRepository;
    private final NotificacionRepository notificacionRepository;

    public VehiculoServiceImpl(
            VehiculoRepository vehiculoRepository,
            UsuarioRepository usuarioRepository,
            ViajeRepository viajeRepository,
            ReservaRepository reservaRepository,
            CalificacionRepository calificacionRepository,
            NotificacionRepository notificacionRepository) {
        this.vehiculoRepository     = vehiculoRepository;
        this.usuarioRepository      = usuarioRepository;
        this.viajeRepository        = viajeRepository;
        this.reservaRepository      = reservaRepository;
        this.calificacionRepository = calificacionRepository;
        this.notificacionRepository = notificacionRepository;
    }

    private Vehiculo toEntity(VehiculoDTO dto) {
        Vehiculo v = new Vehiculo();
        v.setPlaca(dto.getPlaca());
        v.setMarca(dto.getMarca());
        v.setModelo(dto.getModelo());
        v.setCapacidad(dto.getCapacidad());
        Usuario u = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + dto.getIdUsuario()));
        v.setUsuario(u);
        return v;
    }

    @Override
    public List<Vehiculo> listarTodos() { return vehiculoRepository.findAll(); }

    @Override
    public Vehiculo buscarPorId(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado: " + id));
    }

    @Override
    public Vehiculo guardar(VehiculoDTO dto) {
        return vehiculoRepository.save(toEntity(dto));
    }

    @Override
    public Vehiculo actualizar(Long id, VehiculoDTO dto) {
        Vehiculo v = buscarPorId(id);
        v.setPlaca(dto.getPlaca());
        v.setMarca(dto.getMarca());
        v.setModelo(dto.getModelo());
        v.setCapacidad(dto.getCapacidad());
        if (dto.getIdUsuario() != null) {
            Usuario u = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            v.setUsuario(u);
        }
        return vehiculoRepository.save(v);
    }

    /**
     * Eliminación en cascada: calificaciones → reservas → viajes → vehículo
     */
    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!vehiculoRepository.existsById(id))
            throw new RuntimeException("Vehículo no encontrado: " + id);

        // 1. Por cada viaje del vehículo eliminar calificaciones, reservas y notificaciones
        List<Long> idsViajes = viajeRepository.findIdsByIdVehiculo(id);
        for (Long idViaje : idsViajes) {
            calificacionRepository.deleteByIdReservaViaje(idViaje);
            reservaRepository.deleteByIdViaje(idViaje);
            notificacionRepository.deleteByIdViaje(idViaje);
        }

        // 2. Eliminar viajes del vehículo
        viajeRepository.deleteByIdVehiculo(id);

        // 3. Eliminar el vehículo
        vehiculoRepository.deleteById(id);
    }

    @Override
    public List<Vehiculo> buscarPorUsuario(Long idUsuario) {
        return vehiculoRepository.findByIdUsuario(idUsuario);
    }
}