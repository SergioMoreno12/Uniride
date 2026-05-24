package com.example.Uniride.Service;

import com.example.Uniride.DTO.NotificacionDTO;
import com.example.Uniride.Model.Notificacion;
import com.example.Uniride.Repository.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionServiceImpl(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    private Notificacion toEntity(NotificacionDTO dto) {
        Notificacion n = new Notificacion();
        n.setTitulo(dto.getTitulo());
        n.setMensaje(dto.getMensaje());
        n.setDestinatarios(dto.getDestinatarios() != null ? dto.getDestinatarios() : "todos");
        n.setFechaEnvio(dto.getFechaEnvio() != null ? dto.getFechaEnvio() : LocalDateTime.now());
        n.setLeida(false);
        // id_usuario queda null → es broadcast; el repo lo entrega a usuarios por rol
        return n;
    }

    @Override
    public List<Notificacion> listarTodas() {
        return notificacionRepository.findAll();
    }

    @Override
    public Notificacion guardar(NotificacionDTO dto) {
        if (dto.getTitulo() == null || dto.getTitulo().isBlank())
            throw new RuntimeException("El título es obligatorio");
        if (dto.getMensaje() == null || dto.getMensaje().isBlank())
            throw new RuntimeException("El mensaje es obligatorio");
        return notificacionRepository.save(toEntity(dto));
    }

    @Override
    public void eliminar(Long id) {
        if (!notificacionRepository.existsById(id))
            throw new RuntimeException("Notificación no encontrada: " + id);
        notificacionRepository.deleteById(id);
    }

    @Override
    public List<Notificacion> buscarPorDestinatarios(String destinatarios) {
        return notificacionRepository.findByDestinatarios(destinatarios);
    }

    @Override
    public List<Notificacion> buscarPorUsuario(Long idUsuario) {
        return notificacionRepository.findByIdUsuario(idUsuario);
    }

    @Override
    public void marcarLeida(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        n.setLeida(true);
        notificacionRepository.save(n);
    }
}