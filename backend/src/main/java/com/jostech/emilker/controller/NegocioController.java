package com.jostech.emilker.controller;

import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.repository.NegocioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/negocios")
public class NegocioController {

    private final NegocioRepository negocioRepository;

    public NegocioController(NegocioRepository negocioRepository) {
        this.negocioRepository = negocioRepository;
    }

    @GetMapping
    public List<Negocio> listar() {
        return negocioRepository.findAll();
    }

    @GetMapping("/{id}")
    public Negocio verUno(@PathVariable Long id) {
        return negocioRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Negocio no encontrado: " + id));
    }

    // El correo del admin logueado sale del token (Authentication), asi que no
    // hace falta que el frontend mande ningun id: cada admin solo ve el suyo.
    @GetMapping("/mi-negocio")
    public Negocio miNegocio(Authentication auth) {
        return negocioRepository.findByAdminCorreo(auth.getName()).orElseThrow(() ->
                new IllegalArgumentException("No se encontró un negocio para esta cuenta."));
    }

    // CU-03: el admin actualiza los datos de su negocio (nombre, logo, portada, ubicacion, etc.)
    //
    // OJO: el frontend no siempre manda los 11 campos a la vez (ej. el panel
    // "Servicio base" de Ajustes no manda departamento/dirección/ubicación).
    // Por eso cada campo solo se actualiza si vino informado en el body — así
    // un guardado parcial nunca borra silenciosamente lo que otro panel guardó.
    @PutMapping("/mi-negocio")
    public Negocio actualizarMiNegocio(@RequestBody Negocio datos, Authentication auth) {
        Negocio negocio = negocioRepository.findByAdminCorreo(auth.getName()).orElseThrow(() ->
                new IllegalArgumentException("No se encontró un negocio para esta cuenta."));
        if (datos.getNombre() != null) negocio.setNombre(datos.getNombre());
        if (datos.getRubro() != null) negocio.setRubro(datos.getRubro());
        if (datos.getTelefonoFijo() != null) negocio.setTelefonoFijo(datos.getTelefonoFijo());
        if (datos.getDepartamento() != null) negocio.setDepartamento(datos.getDepartamento());
        if (datos.getDireccion() != null) negocio.setDireccion(datos.getDireccion());
        if (datos.getLogoUrl() != null) negocio.setLogoUrl(datos.getLogoUrl());
        if (datos.getPortadaUrl() != null) negocio.setPortadaUrl(datos.getPortadaUrl());
        if (datos.getServicioNombre() != null) negocio.setServicioNombre(datos.getServicioNombre());
        if (datos.getServicioPrecio() > 0) negocio.setServicioPrecio(datos.getServicioPrecio());
        if (datos.getWhatsapp() != null) negocio.setWhatsapp(datos.getWhatsapp());
        if (datos.getNotificacionDestino() != null) negocio.setNotificacionDestino(datos.getNotificacionDestino());
        // Coordenadas geocodificadas a partir de la dirección real (ver Ajustes/registro)
        if (datos.getLatitud() != null) negocio.setLatitud(datos.getLatitud());
        if (datos.getLongitud() != null) negocio.setLongitud(datos.getLongitud());
        return negocioRepository.save(negocio);
    }
}
