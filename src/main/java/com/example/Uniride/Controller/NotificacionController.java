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

    // GET /api/notificaciones
    @GetMapping
    public ResponseEntity<List<Notificacion>> listar() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    // POST /api/notificaciones
    @PostMapping
    public ResponseEntity<Notificacion> crear(@RequestBody NotificacionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionService.guardar(dto));
    }

    // DELETE /api/notificaciones/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            notificacionService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/notificaciones/destinatarios/{dest}
    @GetMapping("/destinatarios/{dest}")
    public ResponseEntity<List<Notificacion>> porDestinatarios(@PathVariable String dest) {
        return ResponseEntity.ok(notificacionService.buscarPorDestinatarios(dest));
    }
}