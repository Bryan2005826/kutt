package com.jostech.emilker.service;

import com.jostech.emilker.dto.CrearCitaRequest;
import com.jostech.emilker.model.Adicional;
import com.jostech.emilker.model.Barbero;
import com.jostech.emilker.model.Cita;
import com.jostech.emilker.model.MetodoPago;
import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.repository.AdicionalRepository;
import com.jostech.emilker.repository.BarberoRepository;
import com.jostech.emilker.repository.CitaRepository;
import com.jostech.emilker.repository.MetodoPagoRepository;
import com.jostech.emilker.repository.NegocioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final AdicionalRepository adicionalRepository;
    private final NegocioRepository negocioRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final BarberoRepository barberoRepository;

    public CitaService(CitaRepository citaRepository, AdicionalRepository adicionalRepository,
                        NegocioRepository negocioRepository, MetodoPagoRepository metodoPagoRepository,
                        BarberoRepository barberoRepository) {
        this.citaRepository = citaRepository;
        this.adicionalRepository = adicionalRepository;
        this.negocioRepository = negocioRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.barberoRepository = barberoRepository;
    }

    // CU-04: agendar cita con servicio base + adicionales elegidos, todo dentro
    // del negocio concreto que el cliente escogio en Descubrir (multi-tenant real)
    public Cita crear(CrearCitaRequest request) {
        if (request.getNegocioId() == null) {
            throw new IllegalArgumentException("Falta indicar a que negocio pertenece la cita.");
        }
        Negocio negocio = negocioRepository.findById(request.getNegocioId()).orElseThrow(() ->
                new IllegalArgumentException("Negocio no encontrado: " + request.getNegocioId()));

        List<String> nombresAdicionales = new ArrayList<>();
        double totalAdicionales = 0;
        if (request.getAdicionalIds() != null) {
            for (Long adicionalId : request.getAdicionalIds()) {
                Adicional adicional = adicionalRepository.findById(adicionalId).orElseThrow(() ->
                        new IllegalArgumentException("Adicional no encontrado: " + adicionalId));
                nombresAdicionales.add(adicional.getNombre());
                totalAdicionales += adicional.getPrecio();
            }
        }

        Cita cita = new Cita();
        cita.setNegocioId(negocio.getId());
        cita.setCliente(request.getCliente());
        cita.setTelefonoCliente(request.getTelefonoCliente());
        cita.setServicio(negocio.getServicioNombre());

        if (request.getBarberoId() != null) {
            Barbero barbero = barberoRepository.findById(request.getBarberoId()).orElseThrow(() ->
                    new IllegalArgumentException("Barbero no encontrado: " + request.getBarberoId()));
            cita.setBarbero(barbero.getNombre());
        }

        cita.setAdicionales(String.join(", ", nombresAdicionales));
        cita.setFecha(request.getFecha());
        cita.setHora(request.getHora());
        cita.setNotas(request.getNotas());
        cita.setEstado("Pendiente");
        cita.setEstadoPago("Pendiente");
        cita.setTotal(negocio.getServicioPrecio() + totalAdicionales);

        return citaRepository.save(cita);
    }

    public Cita cambiarEstado(Long id, String nuevoEstado) {
        Cita cita = citaRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cita no encontrada: " + id));
        cita.setEstado(nuevoEstado);
        return citaRepository.save(cita);
    }

    public Cita reprogramar(Long id, String nuevaFecha, String nuevaHora) {
        Cita cita = citaRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cita no encontrada: " + id));
        cita.setFecha(nuevaFecha);
        cita.setHora(nuevaHora);
        cita.setEstado("Pendiente");
        cita.setRecordatorioEnviado(false);
        return citaRepository.save(cita);
    }

    // CU-08: generar el pago (QR digital real o ticket QR de reserva para efectivo)
    public Cita pagar(Long id, Long metodoPagoId) {
        Cita cita = citaRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cita no encontrada: " + id));
        MetodoPago metodo = metodoPagoRepository.findById(metodoPagoId).orElseThrow(() ->
                new IllegalArgumentException("Metodo de pago no encontrado: " + metodoPagoId));

        cita.setMetodoPago(metodo.getNombre());
        boolean esDigital = "DIGITAL".equalsIgnoreCase(metodo.getTipo());
        cita.setTipoQr(esDigital ? "DIGITAL" : "TICKET");
        cita.setEstadoPago(esDigital ? "Pagado" : "Pendiente");

        return citaRepository.save(cita);
    }
}
