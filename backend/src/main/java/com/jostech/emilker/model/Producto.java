package com.jostech.emilker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private double precio;
    private int existencias;
    private int minimo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getExistencias() { return existencias; }
    public void setExistencias(int existencias) { this.existencias = existencias; }

    public int getMinimo() { return minimo; }
    public void setMinimo(int minimo) { this.minimo = minimo; }
}
