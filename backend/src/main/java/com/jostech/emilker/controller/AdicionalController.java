package com.jostech.emilker.controller;

import com.jostech.emilker.model.Adicional;
import com.jostech.emilker.repository.AdicionalRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adicionales")
public class AdicionalController {

    private final AdicionalRepository adicionalRepository;

    public AdicionalController(AdicionalRepository adicionalRepository) {
        this.adicionalRepository = adicionalRepository;
    }

    @GetMapping
    public List<Adicional> listar() {
        return adicionalRepository.findAll();
    }

    // CU-03: el Super Admin configura los adicionales (barba, cejas, tinte, bigote, etc.)
    @PostMapping
    public Adicional crear(@RequestBody Adicional adicional) {
        return adicionalRepository.save(adicional);
    }

    @PutMapping("/{id}")
    public Adicional editar(@PathVariable Long id, @RequestBody Adicional datos) {
        Adicional a = adicionalRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Adicional no encontrado: " + id));
        a.setNombre(datos.getNombre());
        a.setPrecio(datos.getPrecio());
        return adicionalRepository.save(a);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        adicionalRepository.deleteById(id);
    }
}
