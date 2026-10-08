package com.jostech.emilker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "barberos")
public class Barbero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NUEVO: a que negocio pertenece este empleado (multi-tenant real)
    private Long negocioId;

    private String nombre;
    private String fotoUrl;

    // NUEVO: WhatsApp del peluquero (formato: indicativo+numero, ej 573001234567)
    // para que tambien le llegue el QR con los datos de la cita que le agendaron
    private String whatsapp;

    // true = actualmente en servicio (disponible para que el cliente lo elija)
    private boolean activo = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
