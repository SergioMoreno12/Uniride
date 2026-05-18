package com.example.Uniride.Service;

import com.example.Uniride.DTO.SedeDTO;
import com.example.Uniride.Model.Sede;
import com.example.Uniride.Repository.SedeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SedeServiceImpl implements SedeService {

    private final SedeRepository sedeRepository;

    public SedeServiceImpl(SedeRepository sedeRepository) {
        this.sedeRepository = sedeRepository;
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
    public void eliminar(Long id) {
        if (!sedeRepository.existsById(id))
            throw new RuntimeException("Sede no encontrada con id: " + id);
        sedeRepository.deleteById(id);
    }

    @Override
    public List<Sede> buscarPorCiudad(String ciudad) {
        return sedeRepository.findByciudad(ciudad);
    }
}