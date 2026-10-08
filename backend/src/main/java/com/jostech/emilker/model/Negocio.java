package com.jostech.emilker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "negocios")
public class Negocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // BARBERIA | UNAS | ESTETICA | SPA | OTRO
    private String rubro;

    private String telefonoFijo;
    private String departamento;
    private String direccion;
    private String logoUrl;
    private String portadaUrl;

    // correo del admin dueño de este negocio
    private String adminCorreo;

    private boolean verificado = false;

    // coordenadas para mostrar el negocio en el mapa de Descubrir
    private Double latitud;
    private Double longitud;

    // ---- NUEVO: multi-tenant real. Antes esto era una fila global compartida
    // por todos los negocios (tabla "configuracion"); ahora cada negocio tiene
    // su propio servicio base, precio y WhatsApp para recibir el QR ----
    private String servicioNombre = "Servicio base";
    private double servicioPrecio = 0;
    private String whatsapp = "";
    private String notificacionDestino = "";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRubro() { return rubro; }
    public void setRubro(String rubro) { this.rubro = rubro; }

    public String getTelefonoFijo() { return telefonoFijo; }
    public void setTelefonoFijo(String telefonoFijo) { this.telefonoFijo = telefonoFijo; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getPortadaUrl() { return portadaUrl; }
    public void setPortadaUrl(String portadaUrl) { this.portadaUrl = portadaUrl; }

    public String getAdminCorreo() { return adminCorreo; }
    public void setAdminCorreo(String adminCorreo) { this.adminCorreo = adminCorreo; }

    public boolean isVerificado() { return verificado; }
    public void setVerificado(boolean verificado) { this.verificado = verificado; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getServicioNombre() { return servicioNombre; }
    public void setServicioNombre(String servicioNombre) { this.servicioNombre = servicioNombre; }

    public double getServicioPrecio() { return servicioPrecio; }
    public void setServicioPrecio(double servicioPrecio) { this.servicioPrecio = servicioPrecio; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getNotificacionDestino() { return notificacionDestino; }
    public void setNotificacionDestino(String notificacionDestino) { this.notificacionDestino = notificacionDestino; }
}
