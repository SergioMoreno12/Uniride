package com.example.Uniride.Config;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Logger profesional — Recomendación del profesor
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * ✅ Validaciones de @Valid en @RequestBody — Recomendación del profesor.
     * Captura errores de campos como @NotBlank, @Email, @Size, etc.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> erroresCampos = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Campo inválido",
                        (e1, e2) -> e1
                ));

        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Error de validación");
        body.put("errores", erroresCampos);
        logger.warn("Error de validación en request: {}", erroresCampos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * ✅ Violaciones de restricciones de Bean Validation en parámetros — Recomendación del profesor.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        String mensaje = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));
        logger.warn("Violación de restricción: {}", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildBody(HttpStatus.BAD_REQUEST, mensaje));
    }

    /**
     * ✅ Integridad de datos (duplicados, FK violadas) — Recomendación del profesor.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        logger.error("Violación de integridad de datos: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildBody(HttpStatus.CONFLICT,
                        "No se puede realizar la operación: existen registros relacionados o datos duplicados."));
    }

    /**
     * ✅ Entidad no encontrada — Recomendación del profesor.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        logger.warn("Entidad no encontrada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildBody(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    /**
     * ✅ Acceso denegado (sin permisos) — Recomendación del profesor.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Acceso denegado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildBody(HttpStatus.FORBIDDEN,
                        "No tienes permisos para realizar esta acción."));
    }

    /**
     * Errores de negocio lanzados con RuntimeException desde los servicios.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        logger.warn("Error de negocio: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildBody(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    /**
     * Fallback para cualquier otra excepción no controlada.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        logger.error("Error interno no controlado: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildBody(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error interno del servidor. Contacta al administrador."));
    }

    // ── Helper ────────────────────────────────────────────────────────────
    private Map<String, Object> buildBody(HttpStatus status, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   mensaje);
        return body;
    }
}