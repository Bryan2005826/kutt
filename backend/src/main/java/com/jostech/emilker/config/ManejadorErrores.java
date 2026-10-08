package com.jostech.emilker.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Atrapa cualquier error que se escape de un controlador y lo convierte en una
// respuesta de texto prolija, en vez de dejar que el usuario vea un stacktrace de
// Java. Devuelve texto plano (no un objeto JSON) para que coincida con lo que ya
// espera el frontend en los mensajes de error existentes (err?.error).
@RestControllerAdvice
public class ManejadorErrores {

    // Se usa en todo el proyecto para casos como "cliente no encontrado",
    // "correo ya registrado", "existencias insuficientes", etc.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarSolicitudInvalida(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> manejarAccesoDenegado(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para realizar esta acción.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> manejarCredencialesInvalidas(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos.");
    }

    // Red de seguridad: cualquier otro error inesperado (por ejemplo, un fallo real
    // de conexión a la base de datos) tampoco debe filtrar detalles internos.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarErrorGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Algo salió mal procesando la solicitud. Intenta de nuevo en unos segundos.");
    }
}
