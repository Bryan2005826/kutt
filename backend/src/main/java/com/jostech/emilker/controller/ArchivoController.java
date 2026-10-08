package com.jostech.emilker.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Esto reemplaza tener que pegar una URL a mano: el admin elige una foto de su
// galeria/computador, la mandamos aqui, la guardamos en el servidor, y le
// devolvemos la direccion (URL) lista para usar como fotoUrl.
@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    @Value("${app.upload.dir}")
    private String carpetaSubidas;

    @PostMapping("/subir")
    public Map<String, String> subir(@RequestParam("archivo") MultipartFile archivo, HttpServletRequest request) throws IOException {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("No se recibió ningún archivo.");
        }

        // creamos la carpeta de subidas si todavia no existe
        Path carpeta = Paths.get(carpetaSubidas);
        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }

        // le ponemos un nombre unico para que dos fotos no se pisen entre si
        String nombreOriginal = archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "foto.jpg";
        String extension = nombreOriginal.contains(".") ? nombreOriginal.substring(nombreOriginal.lastIndexOf('.')) : "";
        String nombreGuardado = UUID.randomUUID() + extension;

        Path destino = carpeta.resolve(nombreGuardado);
        Files.copy(archivo.getInputStream(), destino);

        // armamos la URL completa (esquema + host + puerto) para que el frontend
        // la pueda usar directo en un <img src="...">
        String urlBase = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String url = urlBase + "/uploads/" + nombreGuardado;

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("url", url);
        return respuesta;
    }
}
