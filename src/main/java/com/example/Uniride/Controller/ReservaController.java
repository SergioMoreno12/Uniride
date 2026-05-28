package com.example.Uniride.Controller;

import com.example.Uniride.DTO.ReservaDTO;
import com.example.Uniride.Model.Reserva;
import com.example.Uniride.Service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> listar() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try { return ResponseEntity.ok(reservaService.buscarPorId(id)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Reserva>> porUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(reservaService.buscarPorUsuario(idUsuario));
    }

    @GetMapping("/viaje/{idViaje}")
    public ResponseEntity<List<Reserva>> porViaje(@PathVariable Long idViaje) {
        return ResponseEntity.ok(reservaService.buscarPorViaje(idViaje));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ReservaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.guardar(dto));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.confirmar(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        reservaService.cancelar(id);
        return ResponseEntity.ok("Reserva cancelada");
    }

    @PatchMapping("/{id}/calificada")
    public ResponseEntity<Void> marcarCalificada(@PathVariable Long id) {
        reservaService.marcarCalificada(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}