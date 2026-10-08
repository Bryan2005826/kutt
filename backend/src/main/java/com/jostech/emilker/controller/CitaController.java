package com.jostech.emilker.controller;

import com.jostech.emilker.dto.CrearCitaRequest;
import com.jostech.emilker.dto.PagarRequest;
import com.jostech.emilker.dto.ReprogramarRequest;
import com.jostech.emilker.model.Cita;
import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.repository.CitaRepository;
import com.jostech.emilker.repository.NegocioRepository;
import com.jostech.emilker.service.CitaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaRepository citaRepository;
    private final CitaService citaService;
    private final NegocioRepository negocioRepository;

    public CitaController(CitaRepository citaRepository, CitaService citaService, NegocioRepository negocioRepository) {
        this.citaRepository = citaRepository;
        this.citaService = citaService;
        this.negocioRepository = negocioRepository;
    }

    // Multi-tenant: si viene "negocioId" se filtra por ese negocio; si hay un admin
    // logueado se filtra por su propio negocio (Agenda/Ventas/Reportes); si es un
    // cliente, se le devuelven todas (el filtra por su nombre en "Mis citas", ya
    // que un mismo cliente puede tener citas en varios negocios distintos).
    @GetMapping
    public List<Cita> listar(@RequestParam(required = false) Long negocioId, Authentication auth) {
        if (negocioId != null) return citaRepository.findByNegocioId(negocioId);
        if (esAdminNegocio(auth)) {
            Negocio miNegocio = negocioRepository.findByAdminCorreo(auth.getName()).orElse(null);
            if (miNegocio != null) return citaRepository.findByNegocioId(miNegocio.getId());
        }
        return citaRepository.findAll();
    }

    // CU-04: agendar cita (servicio base + adicionales), siempre para un negocio concreto
    @PostMapping
    public Cita crear(@RequestBody CrearCitaRequest request) {
        return citaService.crear(request);
    }

    @PutMapping("/{id}/confirmar")
    public Cita confirmar(@PathVariable Long id) {
        return citaService.cambiarEstado(id, "Confirmada");
    }

    @PutMapping("/{id}/rechazar")
    public Cita rechazar(@PathVariable Long id) {
        return citaService.cambiarEstado(id, "Cancelada");
    }

    @PutMapping("/{id}/cancelar")
    public Cita cancelar(@PathVariable Long id) {
        return citaService.cambiarEstado(id, "Cancelada");
    }

    @PutMapping("/{id}/reprogramar")
    public Cita reprogramar(@PathVariable Long id, @RequestBody ReprogramarRequest request) {
        return citaService.reprogramar(id, request.getFecha(), request.getHora());
    }

    @PutMapping("/{id}/finalizar")
    public Cita finalizar(@PathVariable Long id) {
        return citaService.cambiarEstado(id, "Finalizada");
    }

    // CU-08: generar el pago (QR digital real o ticket QR de reserva)
    @PostMapping("/{id}/pagar")
    public Cita pagar(@PathVariable Long id, @RequestBody PagarRequest request) {
        return citaService.pagar(id, request.getMetodoPagoId());
    }

    private boolean esAdminNegocio(Authentication auth) {
        if (auth == null) return false;
        for (GrantedAuthority a : auth.getAuthorities()) {
            if ("ROLE_ADMIN_NEGOCIO".equals(a.getAuthority())) return true;
        }
        return false;
    }
}
