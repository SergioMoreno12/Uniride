package com.example.Uniride.Service;

import com.example.Uniride.DTO.VehiculoDTO;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Model.Vehiculo;
import com.example.Uniride.Repository.UsuarioRepository;
import com.example.Uniride.Repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;

    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository,
                               UsuarioRepository usuarioRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Vehiculo toEntity(VehiculoDTO dto) {
        Vehiculo v = new Vehiculo();
        v.setPlaca(dto.getPlaca());
        v.setMarca(dto.getMarca());
        v.setModelo(dto.getModelo());
        v.setCapacidad(dto.getCapacidad());
        Usuario u = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + dto.getIdUsuario()));
        v.setUsuario(u);
        return v;
    }

    @Override
    public List<Vehiculo> listarTodos() {
        return vehiculoRepository.findAll();
    }

    @Override
    public Vehiculo buscarPorId(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado con id: " + id));
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
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + dto.getIdUsuario()));
            v.setUsuario(u);
        }
        return vehiculoRepository.save(v);
    }

    @Override
    public void eliminar(Long id) {
        if (!vehiculoRepository.existsById(id))
            throw new RuntimeException("Vehiculo no encontrado con id: " + id);
        vehiculoRepository.deleteById(id);
    }

    @Override
    public List<Vehiculo> buscarPorUsuario(Long idUsuario) {
        return vehiculoRepository.findByIdUsuario(idUsuario);
    }
}