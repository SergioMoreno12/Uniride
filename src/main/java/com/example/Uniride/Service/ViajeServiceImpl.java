package com.example.Uniride.Service;

import com.example.Uniride.DTO.ViajeDTO;
import com.example.Uniride.Model.Notificacion;
import com.example.Uniride.Model.Reserva;
import com.example.Uniride.Model.Sede;
import com.example.Uniride.Model.Vehiculo;
import com.example.Uniride.Model.Viaje;
import com.example.Uniride.Repository.NotificacionRepository;
import com.example.Uniride.Repository.ReservaRepository;
import com.example.Uniride.Repository.SedeRepository;
import com.example.Uniride.Repository.VehiculoRepository;
import com.example.Uniride.Repository.ViajeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ViajeServiceImpl implements ViajeService {

    private final ViajeRepository        viajeRepository;
    private final VehiculoRepository     vehiculoRepository;
    private final SedeRepository         sedeRepository;
    private final ReservaRepository      reservaRepository;
    private final NotificacionRepository notificacionRepository;

    public ViajeServiceImpl(
            ViajeRepository viajeRepository,
            VehiculoRepository vehiculoRepository,
            SedeRepository sedeRepository,
            ReservaRepository reservaRepository,
            NotificacionRepository notificacionRepository) {
        this.viajeRepository        = viajeRepository;
        this.vehiculoRepository     = vehiculoRepository;
        this.sedeRepository         = sedeRepository;
        this.reservaRepository      = reservaRepository;
        this.notificacionRepository = notificacionRepository;
    }

    private Viaje toEntity(ViajeDTO dto) {
        Vehiculo vehiculo = vehiculoRepository.findById(dto.getIdVehiculo())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado: " + dto.getIdVehiculo()));
        Sede sede = sedeRepository.findById(dto.getIdSede())
                .orElseThrow(() -> new RuntimeException("Sede no encontrada: " + dto.getIdSede()));
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
        // Validar solapamiento de horarios
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
                boolean solapa = inicio2.isBefore(fin1) && fin2.isAfter(inicio1);
                if (solapa)
                    throw new RuntimeException(
                            "Ya tienes un viaje activo en ese horario. Elige otra fecha u hora.");
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

    @Override
    public void eliminar(Long id) {
        if (!viajeRepository.existsById(id))
            throw new RuntimeException("Viaje no encontrado: " + id);
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

        // Notificar a todos los pasajeros con reserva confirmada
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