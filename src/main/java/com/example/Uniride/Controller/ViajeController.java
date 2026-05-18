package com.example.Uniride.Controller;

import com.example.Uniride.DTO.ViajeDTO;
import com.example.Uniride.Model.Viaje;
import com.example.Uniride.Service.ViajeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/viajes")
public class ViajeController {

    private final ViajeService viajeService;

    public ViajeController(ViajeService viajeService) {
        this.viajeService = viajeService;
    }

    // GET /api/viajes
    @GetMapping
    public ResponseEntity<List<Viaje>> listar() {
        return ResponseEntity.ok(viajeService.listarTodos());
    }

    // GET /api/viajes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Viaje> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(viajeService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/viajes
    @PostMapping
    public ResponseEntity<Viaje> crear(@RequestBody ViajeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(viajeService.guardar(dto));
    }

    // PUT /api/viajes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Viaje> actualizar(@PathVariable Long id, @RequestBody ViajeDTO dto) {
        try {
            return ResponseEntity.ok(viajeService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/viajes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            viajeService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/viajes/sede/{idSede}
    @GetMapping("/sede/{idSede}")
    public ResponseEntity<List<Viaje>> porSede(@PathVariable Long idSede) {
        return ResponseEntity.ok(viajeService.buscarPorSede(idSede));
    }

    // GET /api/viajes/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Viaje>> porEstado(@PathVariable String estado) {
        return ResponseEntity.ok(viajeService.buscarPorEstado(estado));
    }

    // GET /api/viajes/vehiculo/{idVehiculo}
    @GetMapping("/vehiculo/{idVehiculo}")
    public ResponseEntity<List<Viaje>> porVehiculo(@PathVariable Long idVehiculo) {
        return ResponseEntity.ok(viajeService.buscarPorVehiculo(idVehiculo));
    }
}