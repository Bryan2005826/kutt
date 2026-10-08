package com.jostech.emilker.controller;

import com.jostech.emilker.config.DataSeeder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Endpoint temporal para "rellenar" los negocios de demostracion que falten en una base de
// datos que ya tenia datos reales cuando arranco por primera vez (por eso el DataSeeder normal,
// que solo corre si la tabla de negocios esta vacia, no alcanzo a crearlos). Es seguro llamarlo
// mas de una vez: sembrarNegociosDemo() nunca duplica ni toca negocios reales, solo agrega los
// que falten. Protegido con una clave (variable de entorno SEED_KEY); si no se configura esa
// variable, el endpoint queda deshabilitado (responde 404).
@RestController
@RequestMapping("/api/admin")
public class AdminSeedController {

    private final DataSeeder dataSeeder;

    @Value("${app.admin.seed-key:}")
    private String seedKey;

    public AdminSeedController(DataSeeder dataSeeder) {
        this.dataSeeder = dataSeeder;
    }

    @PostMapping("/sembrar-demo")
    public ResponseEntity<?> sembrarDemo(@RequestHeader(value = "X-Seed-Key", required = false) String clave) {
        if (seedKey == null || seedKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!seedKey.equals(clave)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Clave inválida.");
        }
        int creados = dataSeeder.sembrarNegociosDemo();
        return ResponseEntity.ok("Negocios demo creados: " + creados);
    }
}
