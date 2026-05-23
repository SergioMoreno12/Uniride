package com.example.Uniride.Service;

import com.example.Uniride.DTO.ViajeDTO;
import com.example.Uniride.Model.*;
import com.example.Uniride.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ViajeServiceImpl implements ViajeService {

    private final ViajeRepository        viajeRepository;
    private final VehiculoRepository     vehiculoRepository;
    private final SedeRepository         sedeRepository;
    private final ReservaRepository      reservaRepository;
    private final CalificacionRepository calificacionRepository;
    private final NotificacionRepository notificacionRepository;

    public ViajeServiceImpl(
            ViajeRepository viajeRepository,
            VehiculoRepository vehiculoRepository,
            SedeRepository sedeRepository,
            ReservaRepository reservaRepository,
            CalificacionRepository calificacionRepository,
            NotificacionRepository notificacionRepository) {
        this.viajeRepository        = viajeRepository;
        this.vehiculoRepository     = vehiculoRepository;
        this.sedeRepository         = sedeRepository;
        this.reservaRepository      = reservaRepository;
        this.calificacionRepository = calificacionRepository;
        this.notificacionRepository = notificacionRepository;
    }

    private Viaje toEntity(ViajeDTO dto) {
        Vehiculo vehiculo = vehiculoRepository.findById(dto.getIdVehiculo())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        Sede sede = sedeRepository.findById(dto.getIdSede())
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
        Viaje v = new Viaje();
        v.setOrigen(dto.getOrigen());
        v.setDestino(dto.getDestino());
        v.setFechaHora(dto.getFechaHora());
        v.setHoraLlegada(dto.getHoraLlegada());
        v.setCosto(dto.getCosto());
        v.setEstado(dto.getEstado() != null ? dto.getEstado() : "disponible");
        v.setDescripcionPunto(dto.getDescripcionPunto());
        v.setVehiculo(vehiculo);
        v.setSede(sede);
        return v;
    }

    @Override
    public List<Viaje> listarTodos() { return viajeRepository.findAll(); }

    @Override
    public Viaje buscarPorId(Long id) {
        return viajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado: " + id));
    }

    @Override
    public Viaje guardar(ViajeDTO dto) {
        List<Viaje> viajesActivos = viajeRepository.findByIdVehiculo(dto.getIdVehiculo());
        for (Viaje viajeExistente : viajesActivos) {
            if (viajeExistente.getEstado().equals("disponible") ||
                    viajeExistente.getEstado().equals("lleno")) {
                LocalDateTime inicio1 = viajeExistente.getFechaHora();
                LocalDateTime fin1    = viajeExistente.getHoraLlegada() != null
                        ? viajeExistente.getHoraLlegada() : inicio1.plusHours(2);
                LocalDateTime inicio2 = dto.getFechaHora();
                LocalDateTime fin2    = dto.getHoraLlegada() != null
                        ? dto.getHoraLlegada() : inicio2.plusHours(2);
                if (inicio2.isBefore(fin1) && fin2.isAfter(inicio1))
                    throw new RuntimeException(
                            "Ya tienes un viaje activo en ese horario.");
            }
        }
        return viajeRepository.save(toEntity(dto));
    }

    @Override
    public Viaje actualizar(Long id, ViajeDTO dto) {
        Viaje v = buscarPorId(id);
        v.setOrigen(dto.getOrigen());
        v.setDestino(dto.getDestino());
        v.setFechaHora(dto.getFechaHora());
        if (dto.getHoraLlegada() != null) v.setHoraLlegada(dto.getHoraLlegada());
        v.setCosto(dto.getCosto());
        v.setDescripcionPunto(dto.getDescripcionPunto());
        if (dto.getIdSede() != null) {
            Sede sede = sedeRepository.findById(dto.getIdSede())
                    .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
            v.setSede(sede);
        }
        return viajeRepository.save(v);
    }

    /**
     * Eliminación en cascada: calificaciones → reservas → viaje
     */
    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!viajeRepository.existsById(id))
            throw new RuntimeException("Viaje no encontrado: " + id);

        // 1. Eliminar calificaciones ligadas a reservas de este viaje
        calificacionRepository.deleteByIdReservaViaje(id);

        // 2. Eliminar reservas del viaje
        reservaRepository.deleteByIdViaje(id);

        // 3. Eliminar notificaciones del viaje
        notificacionRepository.deleteByIdViaje(id);

        // 4. Eliminar el viaje
        viajeRepository.deleteById(id);
    }

    @Override
    public List<Viaje> buscarPorSede(Long idSede) {
        return viajeRepository.findByIdSede(idSede);
    }

    @Override
    public List<Viaje> buscarPorEstado(String estado) {
        return viajeRepository.findByEstado(estado);
    }

    @Override
    public List<Viaje> buscarPorVehiculo(Long idVehiculo) {
        return viajeRepository.findByIdVehiculo(idVehiculo);
    }

    @Override
    public List<Viaje> buscarPorCiudad(String ciudad) {
        return viajeRepository.findByOrigenContaining(ciudad);
    }

    @Override
    public Viaje cancelar(Long id) {
        Viaje v = buscarPorId(id);
        v.setEstado("cancelado");
        return viajeRepository.save(v);
    }

    @Override
    public Viaje completar(Long id) {
        Viaje v = buscarPorId(id);
        v.setEstado("completado");
        Viaje completado = viajeRepository.save(v);

        try {
            List<Reserva> reservas = reservaRepository.findConfirmadasByViaje(id);
            for (Reserva r : reservas) {
                Notificacion notif = new Notificacion();
                notif.setTitulo("¿Cómo fue tu viaje? ⭐");
                notif.setMensaje("Tu viaje de " + v.getOrigen() + " a " +
                        v.getSede().getNombreSede() + " ha finalizado. " +
                        "¡Califica a tu conductor " +
                        v.getVehiculo().getUsuario().getNombre() + "!");
                notif.setDestinatarios("pasajero");
                notif.setIdUsuario(r.getUsuario().getIdUsuario());
                notif.setIdViaje(v.getIdViaje());
                notif.setLeida(false);
                notif.setFechaEnvio(LocalDateTime.now());
                notificacionRepository.save(notif);
            }
        } catch (Exception ignored) { }

        return completado;
    }
}