package com.jostech.emilker.controller;

import com.jostech.emilker.model.Configuracion;
import com.jostech.emilker.repository.ConfiguracionRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

    private final ConfiguracionRepository configuracionRepository;

    public ConfiguracionController(ConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    @GetMapping
    public Configuracion obtener() {
        return configuracionRepository.findById(1L).orElseGet(() -> {
            Configuracion nueva = new Configuracion();
            nueva.setId(1L);
            return configuracionRepository.save(nueva);
        });
    }

    // CU-03 / CU-09: el Super Admin edita el servicio base y el destino de notificaciones
    @PutMapping
    public Configuracion actualizar(@RequestBody Configuracion datos) {
        Configuracion actual = obtener();
        actual.setServicioNombre(datos.getServicioNombre());
        actual.setServicioPrecio(datos.getServicioPrecio());
        actual.setNotificacionDestino(datos.getNotificacionDestino());
        return configuracionRepository.save(actual);
    }
}
