package com.example.Uniride.Controller;

import com.example.Uniride.DTO.TelefonoUsuarioDTO;
import com.example.Uniride.Model.TelefonoUsuario;
import com.example.Uniride.Service.TelefonoUsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/telefonos")
public class TelefonoUsuarioController {

    private final TelefonoUsuarioService telefonoService;

    public TelefonoUsuarioController(TelefonoUsuarioService telefonoService) {
        this.telefonoService = telefonoService;
    }

    // GET /api/telefonos
    @GetMapping
    public ResponseEntity<List<TelefonoUsuario>> listar() {
        return ResponseEntity.ok(telefonoService.listarTodos());
    }

    // GET /api/telefonos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TelefonoUsuario> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(telefonoService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/telefonos
    @PostMapping
    public ResponseEntity<TelefonoUsuario> crear(@RequestBody TelefonoUsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(telefonoService.guardar(dto));
    }

    // PUT /api/telefonos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TelefonoUsuario> actualizar(@PathVariable Long id, @RequestBody TelefonoUsuarioDTO dto) {
        try {
            return ResponseEntity.ok(telefonoService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/telefonos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            telefonoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/telefonos/usuario/{idUsuario}
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<TelefonoUsuario>> porUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(telefonoService.buscarPorUsuario(idUsuario));
    }
}