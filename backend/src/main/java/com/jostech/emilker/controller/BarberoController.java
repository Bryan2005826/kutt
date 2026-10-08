package com.jostech.emilker.controller;

import com.jostech.emilker.model.Barbero;
import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.repository.BarberoRepository;
import com.jostech.emilker.repository.NegocioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barberos")
public class BarberoController {

    private final BarberoRepository barberoRepository;
    private final NegocioRepository negocioRepository;

    public BarberoController(BarberoRepository barberoRepository, NegocioRepository negocioRepository) {
        this.barberoRepository = barberoRepository;
        this.negocioRepository = negocioRepository;
    }

    // Multi-tenant: si viene "negocioId" (el cliente viendo un negocio en Descubrir),
    // se filtra por ese negocio; si no viene pero hay un admin logueado (panel de
    // Ajustes), se usa el negocio de ese admin.
    @GetMapping
    public List<Barbero> listar(@RequestParam(required = false) Long negocioId, Authentication auth) {
        if (negocioId != null) return barberoRepository.findByNegocioId(negocioId);
        Negocio miNegocio = negocioDelAdmin(auth);
        return miNegocio != null ? barberoRepository.findByNegocioId(miNegocio.getId()) : List.of();
    }

    // El admin agrega un barbero/empleado en servicio, con su foto y WhatsApp.
    // El negocioId NUNCA se confia del frontend: sale del token del admin logueado.
    @PostMapping
    public Barbero crear(@RequestBody Barbero barbero, Authentication auth) {
        Negocio miNegocio = negocioDelAdmin(auth);
        if (miNegocio == null) throw new IllegalArgumentException("No se encontró un negocio para esta cuenta.");
        barbero.setNegocioId(miNegocio.getId());
        return barberoRepository.save(barbero);
    }

    @PutMapping("/{id}")
    public Barbero editar(@PathVariable Long id, @RequestBody Barbero datos) {
        Barbero b = barberoRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Barbero no encontrado: " + id));
        b.setNombre(datos.getNombre());
        b.setFotoUrl(datos.getFotoUrl());
        b.setWhatsapp(datos.getWhatsapp());
        b.setActivo(datos.isActivo());
        return barberoRepository.save(b);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        barberoRepository.deleteById(id);
    }

    private Negocio negocioDelAdmin(Authentication auth) {
        if (auth == null) return null;
        return negocioRepository.findByAdminCorreo(auth.getName()).orElse(null);
    }
}
