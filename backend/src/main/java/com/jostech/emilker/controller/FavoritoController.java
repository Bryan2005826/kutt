package com.jostech.emilker.controller;

import com.jostech.emilker.model.Favorito;
import com.jostech.emilker.repository.FavoritoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoRepository favoritoRepository;

    public FavoritoController(FavoritoRepository favoritoRepository) {
        this.favoritoRepository = favoritoRepository;
    }

    @GetMapping("/{clienteCorreo}")
    public List<Favorito> listarPorCliente(@PathVariable String clienteCorreo) {
        return favoritoRepository.findByClienteCorreo(clienteCorreo);
    }

    @PostMapping
    public Favorito agregar(@RequestBody Favorito favorito) {
        return favoritoRepository.findByClienteCorreoAndNegocioId(favorito.getClienteCorreo(), favorito.getNegocioId())
                .orElseGet(() -> favoritoRepository.save(favorito));
    }

    @DeleteMapping
    public void quitar(@RequestParam String clienteCorreo, @RequestParam Long negocioId) {
        favoritoRepository.findByClienteCorreoAndNegocioId(clienteCorreo, negocioId)
                .ifPresent(favoritoRepository::delete);
    }
}
