package com.example.Uniride.Service;

import com.example.Uniride.DTO.ReporteDTO;
import com.example.Uniride.Model.Reporte;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Repository.ReporteRepository;
import com.example.Uniride.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteServiceImpl(ReporteRepository reporteRepository,
                              UsuarioRepository usuarioRepository) {
        this.reporteRepository = reporteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Reporte toEntity(ReporteDTO dto) {
        Reporte r = new Reporte();
        r.setTitulo(dto.getTitulo());
        r.setDescripcion(dto.getDescripcion());
        r.setEstado(dto.getEstado() != null ? dto.getEstado() : "pendiente");
        r.setFechaReporte(dto.getFechaReporte() != null ? dto.getFechaReporte() : LocalDate.now());
        Usuario u = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + dto.getIdUsuario()));
        r.setUsuario(u);
        return r;
    }

    @Override
    public List<Reporte> listarTodos() {
        return reporteRepository.findAll();
    }

    @Override
    public Reporte buscarPorId(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado: " + id));
    }

    @Override
    @Transactional
    public Reporte guardar(ReporteDTO dto) {
        return reporteRepository.save(toEntity(dto));
    }

    @Override
    public Reporte actualizar(Long id, ReporteDTO dto) {
        Reporte r = buscarPorId(id);
        r.setTitulo(dto.getTitulo());
        r.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null) r.setEstado(dto.getEstado());
        return reporteRepository.save(r);
    }

    @Override
    public void eliminar(Long id) {
        if (!reporteRepository.existsById(id))
            throw new RuntimeException("Reporte no encontrado: " + id);
        reporteRepository.deleteById(id);
    }

    @Override
    public List<Reporte> buscarPorEstado(String estado) {
        return reporteRepository.findByEstado(estado);
    }

    @Override
    public List<Reporte> buscarPorUsuario(Long idUsuario) {
        return reporteRepository.findByIdUsuario(idUsuario);
    }
}