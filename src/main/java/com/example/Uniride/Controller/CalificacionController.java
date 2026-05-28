package com.example.Uniride.Controller;

import com.example.Uniride.DTO.CalificacionDTO;
import com.example.Uniride.Model.Calificacion;
import com.example.Uniride.Service.CalificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    // FIX: retorna Void en lugar de la entidad Calificacion para evitar
    // LazyInitializationException durante la serialización Jackson
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CalificacionDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(calificacionService.guardar(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/conductor/{idConductor}")
    public ResponseEntity<List<Calificacion>> porConductor(@PathVariable Long idConductor) {
        return ResponseEntity.ok(calificacionService.buscarPorConductor(idConductor));
    }

    @GetMapping("/conductor/{idConductor}/promedio")
    public ResponseEntity<Double> promedio(@PathVariable Long idConductor) {
        return ResponseEntity.ok(calificacionService.promedioConductor(idConductor));
    }

    @GetMapping("/reserva/{idReserva}/calificada")
    public ResponseEntity<Boolean> yaCalificada(@PathVariable Long idReserva) {
        return ResponseEntity.ok(calificacionService.yaCalificada(idReserva));
    }
}