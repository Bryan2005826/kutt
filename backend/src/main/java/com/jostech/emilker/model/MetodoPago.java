package com.jostech.emilker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "metodos_pago")
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NUEVO: a que negocio pertenece este metodo de pago (multi-tenant real)
    private Long negocioId;

    private String nombre;

    // DIGITAL (genera QR de pago real) | EFECTIVO (genera ticket QR de reserva)
    private String tipo;

    // numero de cuenta, alias o billetera (ej: "3001234567", "Bancolombia Ahorros ****4521")
    private String cuenta;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCuenta() { return cuenta; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }
}
