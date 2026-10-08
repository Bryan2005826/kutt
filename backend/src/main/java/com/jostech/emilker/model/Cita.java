package com.jostech.emilker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NUEVO: a que negocio pertenece esta cita (multi-tenant real)
    private Long negocioId;

    private String cliente;
    private String telefonoCliente;
    private String servicio;
    private String barbero;

    // nombres de los adicionales elegidos, separados por coma (ej: "Barba, Cejas")
    private String adicionales;

    private String fecha;
    private String hora;
    private String notas;

    // Pendiente | Confirmada | Finalizada | Cancelada
    private String estado;

    private boolean recordatorioEnviado = false;

    private double total;

    // Pendiente | Pagado
    private String estadoPago = "Pendiente";

    // Nombre del metodo de pago elegido (ej: "Bancolombia", "Nequi", "Efectivo")
    private String metodoPago;

    // DIGITAL (QR de pago real) | TICKET (QR de reserva para pagar en efectivo) | null si aun no paga
    private String tipoQr;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }

    public String getBarbero() { return barbero; }
    public void setBarbero(String barbero) { this.barbero = barbero; }

    public String getAdicionales() { return adicionales; }
    public void setAdicionales(String adicionales) { this.adicionales = adicionales; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean isRecordatorioEnviado() { return recordatorioEnviado; }
    public void setRecordatorioEnviado(boolean recordatorioEnviado) { this.recordatorioEnviado = recordatorioEnviado; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getTipoQr() { return tipoQr; }
    public void setTipoQr(String tipoQr) { this.tipoQr = tipoQr; }
}
