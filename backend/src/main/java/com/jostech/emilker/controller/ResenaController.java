package com.jostech.emilker.controller;

import com.jostech.emilker.model.Resena;
import com.jostech.emilker.repository.ResenaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    private final ResenaRepository resenaRepository;

    public ResenaController(ResenaRepository resenaRepository) {
        this.resenaRepository = resenaRepository;
    }

    @GetMapping
    public List<Resena> listar() {
        return resenaRepository.findAll();
    }

    @GetMapping("/negocio/{negocioId}")
    public List<Resena> listarPorNegocio(@PathVariable Long negocioId) {
        return resenaRepository.findByNegocioId(negocioId);
    }

    // CU: el cliente califica el servicio despues de una cita
    @PostMapping
    public Resena crear(@RequestBody Resena resena) {
        return resenaRepository.save(resena);
    }
}
