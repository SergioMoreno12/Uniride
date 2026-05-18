package com.example.Uniride.Controller;

import com.example.Uniride.DTO.UsuarioDTO;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET /api/usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // GET /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/usuarios
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody UsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(dto));
    }

    // PUT /api/usuarios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        try {
            return ResponseEntity.ok(usuarioService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/usuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/usuarios/correo/{correo}
    @GetMapping("/correo/{correo}")
    public ResponseEntity<Usuario> porCorreo(@PathVariable String correo) {
        try {
            return ResponseEntity.ok(usuarioService.buscarPorCorreo(correo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}