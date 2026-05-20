package com.example.Uniride.Controller;

import com.example.Uniride.DTO.NotificacionDTO;
import com.example.Uniride.Model.Notificacion;
import com.example.Uniride.Service.NotificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listar() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Notificacion>> porUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(notificacionService.buscarPorUsuario(idUsuario));
    }

    @PostMapping
    public ResponseEntity<Notificacion> crear(@RequestBody NotificacionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionService.guardar(dto));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> marcarLeida(@PathVariable Long id) {
        notificacionService.marcarLeida(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try { notificacionService.eliminar(id); return ResponseEntity.noContent().build(); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @GetMapping("/destinatarios/{dest}")
    public ResponseEntity<List<Notificacion>> porDestinatarios(@PathVariable String dest) {
        return ResponseEntity.ok(notificacionService.buscarPorDestinatarios(dest));
    }
}