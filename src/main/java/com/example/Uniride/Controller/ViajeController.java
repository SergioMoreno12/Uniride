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

    @GetMapping
    public ResponseEntity<List<Viaje>> listar() {
        return ResponseEntity.ok(viajeService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Viaje> buscar(@PathVariable Long id) {
        try { return ResponseEntity.ok(viajeService.buscarPorId(id)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ViajeDTO dto) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(viajeService.guardar(dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Viaje> actualizar(@PathVariable Long id, @RequestBody ViajeDTO dto) {
        try { return ResponseEntity.ok(viajeService.actualizar(id, dto)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try { viajeService.eliminar(id); return ResponseEntity.noContent().build(); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
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
        try { return ResponseEntity.ok(viajeService.cancelar(id)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }
}