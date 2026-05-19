package com.example.Uniride.Controller;

import com.example.Uniride.DTO.*;
import com.example.Uniride.Model.Usuario;
import com.example.Uniride.Service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioService usuarioService,
                             PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody UsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        try {
            return ResponseEntity.ok(usuarioService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<Usuario> porCorreo(@PathVariable String correo) {
        try {
            return ResponseEntity.ok(usuarioService.buscarPorCorreo(correo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar perfil (nombre y teléfono)
    @PatchMapping("/{id}/perfil")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Long id,
                                              @RequestBody ActualizarPerfilDTO dto) {
        try {
            return ResponseEntity.ok(usuarioService.actualizarPerfil(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cambiar contraseña
    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<?> cambiarContrasena(@PathVariable Long id,
                                               @RequestBody CambiarContrasenaDTO dto) {
        try {
            usuarioService.cambiarContrasena(id, dto);
            return ResponseEntity.ok("Contraseña actualizada con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Activar o desactivar cuenta
    @PatchMapping("/{id}/activo")
    public ResponseEntity<?> toggleActivo(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.toggleActivo(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}