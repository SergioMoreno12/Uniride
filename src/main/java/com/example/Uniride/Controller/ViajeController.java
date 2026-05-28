package com.example.Uniride.Controller;

import com.example.Uniride.DTO.ViajeDTO;
import com.example.Uniride.Model.Viaje;
import com.example.Uniride.Service.ViajeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    public ResponseEntity<List<Viaje>> listar() {
        return ResponseEntity.ok(viajeService.listarTodos());
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<Viaje>> listarPaginado(Pageable pageable) {
        return ResponseEntity.ok(viajeService.listarPaginado(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try { return ResponseEntity.ok(viajeService.buscarPorId(id)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ViajeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(viajeService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody ViajeDTO dto) {
        return ResponseEntity.ok(viajeService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        viajeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sede/{idSede}")
    public ResponseEntity<List<Viaje>> porSede(@PathVariable Long idSede) {
        return ResponseEntity.ok(viajeService.buscarPorSede(idSede));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Viaje>> porEstado(@PathVariable String estado) {
        return ResponseEntity.ok(viajeService.buscarPorEstado(estado));
    }

    @GetMapping("/vehiculo/{idVehiculo}")
    public ResponseEntity<List<Viaje>> porVehiculo(@PathVariable Long idVehiculo) {
        return ResponseEntity.ok(viajeService.buscarPorVehiculo(idVehiculo));
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<Viaje>> porCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(viajeService.buscarPorCiudad(ciudad));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(viajeService.cancelar(id));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<?> completar(@PathVariable Long id) {
        return ResponseEntity.ok(viajeService.completar(id));
    }
}