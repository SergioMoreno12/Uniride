package com.example.Uniride.Controller;

import com.example.Uniride.DTO.SedeDTO;
import com.example.Uniride.Model.Sede;
import com.example.Uniride.Service.SedeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    private final SedeService sedeService;

    public SedeController(SedeService sedeService) {
        this.sedeService = sedeService;
    }

    // GET /api/sedes
    @GetMapping
    public ResponseEntity<List<Sede>> listar() {
        return ResponseEntity.ok(sedeService.listarTodas());
    }

    // GET /api/sedes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Sede> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(sedeService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/sedes
    @PostMapping
    public ResponseEntity<Sede> crear(@RequestBody SedeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sedeService.guardar(dto));
    }

    // PUT /api/sedes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Sede> actualizar(@PathVariable Long id, @RequestBody SedeDTO dto) {
        try {
            return ResponseEntity.ok(sedeService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/sedes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            sedeService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/sedes/ciudad/{ciudad}
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<Sede>> porCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(sedeService.buscarPorCiudad(ciudad));
    }
}