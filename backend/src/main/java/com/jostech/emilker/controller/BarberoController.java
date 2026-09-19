package com.jostech.emilker.controller;

import com.jostech.emilker.model.Barbero;
import com.jostech.emilker.repository.BarberoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barberos")
public class BarberoController {

    private final BarberoRepository barberoRepository;

    public BarberoController(BarberoRepository barberoRepository) {
        this.barberoRepository = barberoRepository;
    }

    @GetMapping
    public List<Barbero> listar() {
        return barberoRepository.findAll();
    }

    // El Super Admin agrega un barbero en servicio, con su foto
    @PostMapping
    public Barbero crear(@RequestBody Barbero barbero) {
        return barberoRepository.save(barbero);
    }

    @PutMapping("/{id}")
    public Barbero editar(@PathVariable Long id, @RequestBody Barbero datos) {
        Barbero b = barberoRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Barbero no encontrado: " + id));
        b.setNombre(datos.getNombre());
        b.setFotoUrl(datos.getFotoUrl());
        b.setActivo(datos.isActivo());
        return barberoRepository.save(b);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        barberoRepository.deleteById(id);
    }
}
