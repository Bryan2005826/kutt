package com.jostech.emilker.controller;

import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.repository.NegocioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/negocios")
public class NegocioController {

    private final NegocioRepository negocioRepository;

    public NegocioController(NegocioRepository negocioRepository) {
        this.negocioRepository = negocioRepository;
    }

    @GetMapping
    public List<Negocio> listar() {
        return negocioRepository.findAll();
    }
}
