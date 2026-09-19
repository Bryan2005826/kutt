package com.jostech.emilker.controller;

import com.jostech.emilker.dto.CrearCitaRequest;
import com.jostech.emilker.dto.PagarRequest;
import com.jostech.emilker.dto.ReprogramarRequest;
import com.jostech.emilker.model.Cita;
import com.jostech.emilker.repository.CitaRepository;
import com.jostech.emilker.service.CitaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaRepository citaRepository;
    private final CitaService citaService;

    public CitaController(CitaRepository citaRepository, CitaService citaService) {
        this.citaRepository = citaRepository;
        this.citaService = citaService;
    }

    @GetMapping
    public List<Cita> listar() {
        return citaRepository.findAll();
    }

    // CU-04: agendar cita (servicio base + adicionales)
    @PostMapping
    public Cita crear(@RequestBody CrearCitaRequest request) {
        return citaService.crear(request);
    }

    // CU-06: el Super Admin acepta la cita (verifico disponibilidad)
    @PutMapping("/{id}/confirmar")
    public Cita confirmar(@PathVariable Long id) {
        return citaService.cambiarEstado(id, "Confirmada");
    }

    // CU-06: el Super Admin rechaza la cita
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

    // El Super Admin marca la cita como atendida
    @PutMapping("/{id}/finalizar")
    public Cita finalizar(@PathVariable Long id) {
        return citaService.cambiarEstado(id, "Finalizada");
    }

    // CU-08: generar el pago (QR digital real o ticket QR de reserva)
    @PostMapping("/{id}/pagar")
    public Cita pagar(@PathVariable Long id, @RequestBody PagarRequest request) {
        return citaService.pagar(id, request.getMetodoPagoId());
    }
}
