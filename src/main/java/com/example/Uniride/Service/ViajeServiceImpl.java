package com.example.Uniride.Service;

import com.example.Uniride.DTO.ViajeDTO;
import com.example.Uniride.Model.Sede;
import com.example.Uniride.Model.Vehiculo;
import com.example.Uniride.Model.Viaje;
import com.example.Uniride.Repository.SedeRepository;
import com.example.Uniride.Repository.VehiculoRepository;
import com.example.Uniride.Repository.ViajeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ViajeServiceImpl implements ViajeService {

    private final ViajeRepository viajeRepository;
    private final VehiculoRepository vehiculoRepository;
    private final SedeRepository sedeRepository;

    public ViajeServiceImpl(ViajeRepository viajeRepository,
                            VehiculoRepository vehiculoRepository,
                            SedeRepository sedeRepository) {
        this.viajeRepository = viajeRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.sedeRepository = sedeRepository;
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
        // Validar que el conductor no tenga un viaje activo en el mismo horario
        List<Viaje> viajesActivos = viajeRepository.findByIdVehiculo(dto.getIdVehiculo());
        for (Viaje viajeExistente : viajesActivos) {
            if (viajeExistente.getEstado().equals("disponible") ||
                    viajeExistente.getEstado().equals("lleno")) {
                LocalDateTime inicio1 = viajeExistente.getFechaHora();
                LocalDateTime fin1    = viajeExistente.getHoraLlegada() != null
                        ? viajeExistente.getHoraLlegada()
                        : inicio1.plusHours(2);
                LocalDateTime inicio2 = dto.getFechaHora();
                LocalDateTime fin2    = dto.getHoraLlegada() != null
                        ? dto.getHoraLlegada()
                        : inicio2.plusHours(2);
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
    public Viaje cancelar(Long id) {
        Viaje v = buscarPorId(id);
        v.setEstado("cancelado");
        return viajeRepository.save(v);
    }

    @Override
    public List<Viaje> buscarPorCiudad(String ciudad) {
        return viajeRepository.findByOrigenContaining(ciudad);
    }
}