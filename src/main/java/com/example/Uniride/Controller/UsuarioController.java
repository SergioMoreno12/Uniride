package com.example.Uniride.Controller;

import com.example.Uniride.DTO.ActualizarPerfilDTO;
import com.example.Uniride.DTO.CambiarContrasenaDTO;
import com.example.Uniride.DTO.UsuarioDTO;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<Usuario>> listarPaginado(Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarPaginado(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try { return ResponseEntity.ok(usuarioService.buscarPorId(id)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<?> buscarPorCorreo(@PathVariable String correo) {
        try { return ResponseEntity.ok(usuarioService.buscarPorCorreo(correo)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioDTO dto) {
        // ✅ @Valid — Recomendación del profesor
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(dto));
    }

    @PatchMapping("/{id}/perfil")
    public ResponseEntity<?> actualizarPerfil(
            @PathVariable Long id,
            @RequestBody ActualizarPerfilDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(id, dto));
    }

    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<Map<String, String>> cambiarContrasena(
            @PathVariable Long id,
            @RequestBody CambiarContrasenaDTO dto) {
        usuarioService.cambiarContrasena(id, dto);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada"));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<?> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.toggleActivo(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody ActualizarPerfilDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}