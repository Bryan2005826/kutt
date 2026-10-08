package com.jostech.emilker.controller;

import com.jostech.emilker.model.Adicional;
import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.repository.AdicionalRepository;
import com.jostech.emilker.repository.NegocioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adicionales")
public class AdicionalController {

    private final AdicionalRepository adicionalRepository;
    private final NegocioRepository negocioRepository;

    public AdicionalController(AdicionalRepository adicionalRepository, NegocioRepository negocioRepository) {
        this.adicionalRepository = adicionalRepository;
        this.negocioRepository = negocioRepository;
    }

    @GetMapping
    public List<Adicional> listar(@RequestParam(required = false) Long negocioId, Authentication auth) {
        if (negocioId != null) return adicionalRepository.findByNegocioId(negocioId);
        Negocio miNegocio = negocioDelAdmin(auth);
        return miNegocio != null ? adicionalRepository.findByNegocioId(miNegocio.getId()) : List.of();
    }

    // CU-03: el admin configura los adicionales de su negocio (barba, cejas, tinte, etc.)
    @PostMapping
    public Adicional crear(@RequestBody Adicional adicional, Authentication auth) {
        Negocio miNegocio = negocioDelAdmin(auth);
        if (miNegocio == null) throw new IllegalArgumentException("No se encontró un negocio para esta cuenta.");
        adicional.setNegocioId(miNegocio.getId());
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

    private Negocio negocioDelAdmin(Authentication auth) {
        if (auth == null) return null;
        return negocioRepository.findByAdminCorreo(auth.getName()).orElse(null);
    }
}
