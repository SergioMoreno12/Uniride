package com.example.Uniride.Controller;

import com.example.Uniride.DTO.ReservaDTO;
import com.example.Uniride.Model.Reserva;
import com.example.Uniride.Service.ReservaService;
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

    // GET /api/reservas
    @GetMapping
    public ResponseEntity<List<Reserva>> listar() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    // GET /api/reservas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservaService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/reservas
    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestBody ReservaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.guardar(dto));
    }

    // PUT /api/reservas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Reserva> actualizar(@PathVariable Long id, @RequestBody ReservaDTO dto) {
        try {
            return ResponseEntity.ok(reservaService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/reservas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            reservaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/reservas/usuario/{idUsuario}
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Reserva>> porUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(reservaService.buscarPorUsuario(idUsuario));
    }

    // GET /api/reservas/viaje/{idViaje}
    @GetMapping("/viaje/{idViaje}")
    public ResponseEntity<List<Reserva>> porViaje(@PathVariable Long idViaje) {
        return ResponseEntity.ok(reservaService.buscarPorViaje(idViaje));
    }

    // GET /api/reservas/viaje/{idViaje}/confirmadas
    @GetMapping("/viaje/{idViaje}/confirmadas")
    public ResponseEntity<List<Reserva>> confirmadasPorViaje(@PathVariable Long idViaje) {
        return ResponseEntity.ok(reservaService.confirmadasPorViaje(idViaje));
    }
}