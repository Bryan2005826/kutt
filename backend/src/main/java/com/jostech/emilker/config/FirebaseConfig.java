package com.jostech.emilker.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Inicializa Firebase Admin SDK, que es lo que le permite al backend VERIFICAR
// de verdad (no solo confiar a ciegas) el token que manda el frontend despues
// de que alguien inicia sesion con Google o Facebook.
//
// Ojo: si el archivo de credenciales todavia no existe (por ejemplo, en la
// primera vez que alguien clona el proyecto y no lo ha configurado), el
// backend igual arranca con normalidad; simplemente el login con
// Google/Facebook no va a funcionar hasta que se agregue el archivo.
@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${app.firebase.credentials-path}")
    private String rutaCredenciales;

    @Bean
    public boolean firebaseInicializado() {
        Path archivo = Path.of(rutaCredenciales);
        if (!Files.exists(archivo)) {
            log.warn("No se encontró el archivo de credenciales de Firebase en '{}'. "
                    + "El login con Google/Facebook no funcionará hasta que lo agregues.", rutaCredenciales);
            return false;
        }
        try (FileInputStream serviceAccount = new FileInputStream(archivo.toFile())) {
            FirebaseOptions opciones = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(opciones);
            }
            log.info("Firebase Admin SDK inicializado correctamente.");
            return true;
        } catch (IOException e) {
            log.error("No se pudo inicializar Firebase Admin SDK: {}", e.getMessage());
            return false;
        }
    }
}
