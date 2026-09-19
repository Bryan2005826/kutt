package com.jostech.emilker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracion")
public class Configuracion {

    @Id
    private Long id = 1L;

    private String servicioNombre = "Corte b\u00e1sico";
    private double servicioPrecio = 15000;

    // correo o celular donde llegan las notificaciones de pago/citas al Super Admin
    private String notificacionDestino = "";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getServicioNombre() { return servicioNombre; }
    public void setServicioNombre(String servicioNombre) { this.servicioNombre = servicioNombre; }

    public double getServicioPrecio() { return servicioPrecio; }
    public void setServicioPrecio(double servicioPrecio) { this.servicioPrecio = servicioPrecio; }

    public String getNotificacionDestino() { return notificacionDestino; }
    public void setNotificacionDestino(String notificacionDestino) { this.notificacionDestino = notificacionDestino; }
}
