package com.jostech.emilker.controller;

import com.jostech.emilker.model.MetodoPago;
import com.jostech.emilker.repository.MetodoPagoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metodos-pago")
public class MetodoPagoController {

    private final MetodoPagoRepository metodoPagoRepository;

    public MetodoPagoController(MetodoPagoRepository metodoPagoRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
    }

    @GetMapping
    public List<MetodoPago> listar() {
        return metodoPagoRepository.findAll();
    }

    // CU-09: el Super Admin configura las cuentas/billeteras disponibles para el cobro
    @PostMapping
    public MetodoPago crear(@RequestBody MetodoPago metodo) {
        return metodoPagoRepository.save(metodo);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        metodoPagoRepository.deleteById(id);
    }
}
