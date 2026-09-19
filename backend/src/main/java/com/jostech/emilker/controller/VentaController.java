package com.jostech.emilker.controller;

import com.jostech.emilker.dto.CompraRequest;
import com.jostech.emilker.dto.PagarRequest;
import com.jostech.emilker.model.Venta;
import com.jostech.emilker.repository.VentaRepository;
import com.jostech.emilker.service.VentaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaRepository ventaRepository;
    private final VentaService ventaService;

    public VentaController(VentaRepository ventaRepository, VentaService ventaService) {
        this.ventaRepository = ventaRepository;
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<Venta> listar() {
        return ventaRepository.findAll();
    }

    // Registro manual de venta desde el panel del Super Admin
    @PostMapping
    public Venta crear(@RequestBody Venta venta) {
        return ventaRepository.save(venta);
    }

    // CU-07: compra de productos independiente, hecha por el cliente
    @PostMapping("/compra")
    public Venta comprar(@RequestBody CompraRequest request) {
        return ventaService.comprar(request);
    }

    // CU-08: generar el pago de la compra
    @PostMapping("/{id}/pagar")
    public Venta pagar(@PathVariable Long id, @RequestBody PagarRequest request) {
        return ventaService.pagar(id, request.getMetodoPagoId());
    }
}
