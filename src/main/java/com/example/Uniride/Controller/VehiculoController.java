package com.example.Uniride.Controller;

import com.example.Uniride.DTO.VehiculoDTO;
import com.example.Uniride.Model.Vehiculo;
import com.example.Uniride.Service.VehiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    // GET /api/vehiculos
    @GetMapping
    public ResponseEntity<List<Vehiculo>> listar() {
        return ResponseEntity.ok(vehiculoService.listarTodos());
    }

    // GET /api/vehiculos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(vehiculoService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/vehiculos
    @PostMapping
    public ResponseEntity<Vehiculo> crear(@RequestBody VehiculoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.guardar(dto));
    }

    // PUT /api/vehiculos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> actualizar(@PathVariable Long id, @RequestBody VehiculoDTO dto) {
        try {
            return ResponseEntity.ok(vehiculoService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/vehiculos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            vehiculoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/vehiculos/usuario/{idUsuario}
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Vehiculo>> porUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(vehiculoService.buscarPorUsuario(idUsuario));
    }
}