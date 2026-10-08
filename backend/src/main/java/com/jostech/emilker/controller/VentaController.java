package com.jostech.emilker.controller;

import com.jostech.emilker.dto.CompraRequest;
import com.jostech.emilker.dto.PagarRequest;
import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.model.Venta;
import com.jostech.emilker.repository.NegocioRepository;
import com.jostech.emilker.repository.VentaRepository;
import com.jostech.emilker.service.VentaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaRepository ventaRepository;
    private final VentaService ventaService;
    private final NegocioRepository negocioRepository;

    public VentaController(VentaRepository ventaRepository, VentaService ventaService, NegocioRepository negocioRepository) {
        this.ventaRepository = ventaRepository;
        this.ventaService = ventaService;
        this.negocioRepository = negocioRepository;
    }

    @GetMapping
    public List<Venta> listar(@RequestParam(required = false) Long negocioId, Authentication auth) {
        if (negocioId != null) return ventaRepository.findByNegocioId(negocioId);
        if (esAdminNegocio(auth)) {
            Negocio miNegocio = negocioRepository.findByAdminCorreo(auth.getName()).orElse(null);
            if (miNegocio != null) return ventaRepository.findByNegocioId(miNegocio.getId());
        }
        return ventaRepository.findAll();
    }

    // Registro manual de venta desde el panel del admin
    @PostMapping
    public Venta crear(@RequestBody Venta venta) {
        return ventaRepository.save(venta);
    }

    // CU-07: compra de productos independiente, hecha por el cliente, para un negocio concreto
    @PostMapping("/compra")
    public Venta comprar(@RequestBody CompraRequest request) {
        return ventaService.comprar(request);
    }

    // CU-08: generar el pago de la compra
    @PostMapping("/{id}/pagar")
    public Venta pagar(@PathVariable Long id, @RequestBody PagarRequest request) {
        return ventaService.pagar(id, request.getMetodoPagoId());
    }

    private boolean esAdminNegocio(Authentication auth) {
        if (auth == null) return false;
        for (GrantedAuthority a : auth.getAuthorities()) {
            if ("ROLE_ADMIN_NEGOCIO".equals(a.getAuthority())) return true;
        }
        return false;
    }
}
