package com.example.Uniride.Service;

import com.example.Uniride.DTO.SedeDTO;
import com.example.Uniride.Model.Sede;
import com.example.Uniride.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SedeServiceImpl implements SedeService {

    private final SedeRepository         sedeRepository;
    private final ViajeRepository        viajeRepository;
    private final ReservaRepository      reservaRepository;
    private final CalificacionRepository calificacionRepository;
    private final NotificacionRepository notificacionRepository;

    public SedeServiceImpl(SedeRepository sedeRepository,
                           ViajeRepository viajeRepository,
                           ReservaRepository reservaRepository,
                           CalificacionRepository calificacionRepository,
                           NotificacionRepository notificacionRepository) {
        this.sedeRepository         = sedeRepository;
        this.viajeRepository        = viajeRepository;
        this.reservaRepository      = reservaRepository;
        this.calificacionRepository = calificacionRepository;
        this.notificacionRepository = notificacionRepository;
    }

    private Sede toEntity(SedeDTO dto) {
        Sede s = new Sede();
        s.setNombreSede(dto.getNombreSede());
        s.setCiudad(dto.getCiudad());
        return s;
    }

    @Override
    public List<Sede> listarTodas() {
        return sedeRepository.findAll();
    }

    @Override
    public Sede buscarPorId(Long id) {
        return sedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada con id: " + id));
    }

    @Override
    public Sede guardar(SedeDTO dto) {
        if (dto.getNombreSede() == null || dto.getNombreSede().isBlank())
            throw new RuntimeException("El nombre de la sede es obligatorio");
        if (dto.getCiudad() == null || dto.getCiudad().isBlank())
            throw new RuntimeException("La ciudad es obligatoria");
        return sedeRepository.save(toEntity(dto));
    }

    @Override
    public Sede actualizar(Long id, SedeDTO dto) {
        Sede sede = buscarPorId(id);
        sede.setNombreSede(dto.getNombreSede());
        sede.setCiudad(dto.getCiudad());
        return sedeRepository.save(sede);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!sedeRepository.existsById(id))
            throw new RuntimeException("Sede no encontrada con id: " + id);

        List<Long> idsViajes = viajeRepository.findIdsBySede(id);
        for (Long idViaje : idsViajes) {
            calificacionRepository.deleteByIdReservaViaje(idViaje);
            reservaRepository.deleteByIdViaje(idViaje);
            notificacionRepository.deleteByIdViaje(idViaje);
        }
        viajeRepository.deleteBySede(id);

        sedeRepository.deleteById(id);
    }

    @Override
    public List<Sede> buscarPorCiudad(String ciudad) {
        return sedeRepository.findByciudad(ciudad);
    }
}