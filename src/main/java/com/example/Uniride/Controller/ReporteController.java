package com.example.Uniride.Controller;

import com.example.Uniride.DTO.ReporteDTO;
import com.example.Uniride.Model.Reporte;
import com.example.Uniride.Service.ReporteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    // GET /api/reportes
    @GetMapping
    public ResponseEntity<List<Reporte>> listar() {
        return ResponseEntity.ok(reporteService.listarTodos());
    }

    // GET /api/reportes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Reporte> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reporteService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/reportes
    @PostMapping
    public ResponseEntity<Void> crear(@RequestBody ReporteDTO dto) {
        try {
            reporteService.guardar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // PUT /api/reportes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Reporte> actualizar(@PathVariable Long id, @RequestBody ReporteDTO dto) {
        try {
            return ResponseEntity.ok(reporteService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/reportes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            reporteService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/reportes/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Reporte>> porEstado(@PathVariable String estado) {
        return ResponseEntity.ok(reporteService.buscarPorEstado(estado));
    }

    // GET /api/reportes/usuario/{idUsuario}
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Reporte>> porUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(reporteService.buscarPorUsuario(idUsuario));
    }
}