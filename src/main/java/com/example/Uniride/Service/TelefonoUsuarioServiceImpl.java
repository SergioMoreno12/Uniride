package com.example.Uniride.Service;

import com.example.Uniride.DTO.TelefonoUsuarioDTO;
import com.example.Uniride.Model.TelefonoUsuario;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.TelefonoUsuarioRepository;
import com.example.Uniride.Repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TelefonoUsuarioServiceImpl implements TelefonoUsuarioService {

    private final TelefonoUsuarioRepository telefonoRepository;
    private final UsuarioRepository usuarioRepository;

    public TelefonoUsuarioServiceImpl(TelefonoUsuarioRepository telefonoRepository,
                                      UsuarioRepository usuarioRepository) {
        this.telefonoRepository = telefonoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private TelefonoUsuario toEntity(TelefonoUsuarioDTO dto) {
        TelefonoUsuario t = new TelefonoUsuario();
        t.setTelefono(dto.getTelefono());
        Usuario u = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + dto.getIdUsuario()));
        t.setUsuario(u);
        return t;
    }

    @Override
    public List<TelefonoUsuario> listarTodos() {
        return telefonoRepository.findAll();
    }

    @Override
    public TelefonoUsuario buscarPorId(Long id) {
        return telefonoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Telefono no encontrado con id: " + id));
    }

    @Override
    public TelefonoUsuario guardar(TelefonoUsuarioDTO dto) {
        return telefonoRepository.save(toEntity(dto));
    }

    @Override
    public TelefonoUsuario actualizar(Long id, TelefonoUsuarioDTO dto) {
        TelefonoUsuario t = buscarPorId(id);
        t.setTelefono(dto.getTelefono());
        if (dto.getIdUsuario() != null) {
            Usuario u = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + dto.getIdUsuario()));
            t.setUsuario(u);
        }
        return telefonoRepository.save(t);
    }

    @Override
    public void eliminar(Long id) {
        if (!telefonoRepository.existsById(id))
            throw new RuntimeException("Telefono no encontrado con id: " + id);
        telefonoRepository.deleteById(id);
    }

    @Override
    public List<TelefonoUsuario> buscarPorUsuario(Long idUsuario) {
        return telefonoRepository.findByIdUsuario(idUsuario);
    }
}