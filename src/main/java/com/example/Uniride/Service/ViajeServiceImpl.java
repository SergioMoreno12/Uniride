package com.example.Uniride.Service;

import com.example.Uniride.DTO.ViajeDTO;
import com.example.Uniride.Model.Sede;
import com.example.Uniride.Model.Vehiculo;
import com.example.Uniride.Model.Viaje;
import com.example.Uniride.Repository.SedeRepository;
import com.example.Uniride.Repository.VehiculoRepository;
import com.example.Uniride.Repository.ViajeRepository;
import org.springframework.stereotype.Service;
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
        Viaje v = new Viaje();
        v.setOrigen(dto.getOrigen());
        v.setDestino(dto.getDestino());
        v.setFechaHora(dto.getFechaHora());
        v.setCosto(dto.getCosto());
        v.setEstado(dto.getEstado() != null ? dto.getEstado() : "disponible");
        Vehiculo vehiculo = vehiculoRepository.findById(dto.getIdVehiculo())
                .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado con id: " + dto.getIdVehiculo()));
        v.setVehiculo(vehiculo);
        Sede sede = sedeRepository.findById(dto.getIdSede())
                .orElseThrow(() -> new RuntimeException("Sede no encontrada con id: " + dto.getIdSede()));
        v.setSede(sede);
        return v;
    }

    @Override
    public List<Viaje> listarTodos() {
        return viajeRepository.findAll();
    }

    @Override
    public Viaje buscarPorId(Long id) {
        return viajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado con id: " + id));
    }

    @Override
    public Viaje guardar(ViajeDTO dto) {
        return viajeRepository.save(toEntity(dto));
    }

    @Override
    public Viaje actualizar(Long id, ViajeDTO dto) {
        Viaje v = buscarPorId(id);
        v.setOrigen(dto.getOrigen());
        v.setDestino(dto.getDestino());
        v.setFechaHora(dto.getFechaHora());
        v.setCosto(dto.getCosto());
        v.setEstado(dto.getEstado());
        if (dto.getIdVehiculo() != null) {
            Vehiculo vehiculo = vehiculoRepository.findById(dto.getIdVehiculo())
                    .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado con id: " + dto.getIdVehiculo()));
            v.setVehiculo(vehiculo);
        }
        if (dto.getIdSede() != null) {
            Sede sede = sedeRepository.findById(dto.getIdSede())
                    .orElseThrow(() -> new RuntimeException("Sede no encontrada con id: " + dto.getIdSede()));
            v.setSede(sede);
        }
        return viajeRepository.save(v);
    }

    @Override
    public void eliminar(Long id) {
        if (!viajeRepository.existsById(id))
            throw new RuntimeException("Viaje no encontrado con id: " + id);
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
}