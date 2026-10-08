package com.jostech.emilker.controller;

import com.jostech.emilker.model.MetodoPago;
import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.repository.MetodoPagoRepository;
import com.jostech.emilker.repository.NegocioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metodos-pago")
public class MetodoPagoController {

    private final MetodoPagoRepository metodoPagoRepository;
    private final NegocioRepository negocioRepository;

    public MetodoPagoController(MetodoPagoRepository metodoPagoRepository, NegocioRepository negocioRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
        this.negocioRepository = negocioRepository;
    }

    @GetMapping
    public List<MetodoPago> listar(@RequestParam(required = false) Long negocioId, Authentication auth) {
        if (negocioId != null) return metodoPagoRepository.findByNegocioId(negocioId);
        Negocio miNegocio = negocioDelAdmin(auth);
        return miNegocio != null ? metodoPagoRepository.findByNegocioId(miNegocio.getId()) : List.of();
    }

    // CU-09: el admin configura las cuentas/billeteras disponibles para el cobro de su negocio
    @PostMapping
    public MetodoPago crear(@RequestBody MetodoPago metodo, Authentication auth) {
        Negocio miNegocio = negocioDelAdmin(auth);
        if (miNegocio == null) throw new IllegalArgumentException("No se encontró un negocio para esta cuenta.");
        metodo.setNegocioId(miNegocio.getId());
        return metodoPagoRepository.save(metodo);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        metodoPagoRepository.deleteById(id);
    }

    private Negocio negocioDelAdmin(Authentication auth) {
        if (auth == null) return null;
        return negocioRepository.findByAdminCorreo(auth.getName()).orElse(null);
    }
}
