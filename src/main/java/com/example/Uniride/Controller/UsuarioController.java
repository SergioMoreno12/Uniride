package com.example.Uniride.Controller;

import com.example.Uniride.DTO.ActualizarPerfilDTO;
import com.example.Uniride.DTO.CambiarContrasenaDTO;
import com.example.Uniride.DTO.UsuarioDTO;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Service.UsuarioService;
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
    public ResponseEntity<?> crear(@RequestBody UsuarioDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/perfil")
    public ResponseEntity<?> actualizarPerfil(
            @PathVariable Long id,
            @RequestBody ActualizarPerfilDTO dto) {
        try { return ResponseEntity.ok(usuarioService.actualizarPerfil(id, dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<?> cambiarContrasena(
            @PathVariable Long id,
            @RequestBody CambiarContrasenaDTO dto) {
        try {
            usuarioService.cambiarContrasena(id, dto);
            return ResponseEntity.ok("Contraseña actualizada");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<?> toggleActivo(@PathVariable Long id) {
        try { return ResponseEntity.ok(usuarioService.toggleActivo(id)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            ActualizarPerfilDTO dto = new ActualizarPerfilDTO();
            dto.setNombre(body.get("nombre"));
            dto.setTelefono(body.get("telefono"));
            dto.setRol(body.get("rol"));
            return ResponseEntity.ok(usuarioService.actualizarPerfil(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Distingue entre no encontrado y error de restricción
            if (e.getMessage() != null && e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            // Error de constraint (tiene reservas, viajes, etc.)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: el usuario tiene registros asociados.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: el usuario tiene registros asociados.");
        }
    }
}