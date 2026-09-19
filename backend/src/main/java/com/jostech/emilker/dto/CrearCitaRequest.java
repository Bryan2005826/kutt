package com.jostech.emilker.dto;

import java.util.List;

public class CrearCitaRequest {
    private String cliente;
    private String telefonoCliente;
    private Long barberoId;
    private String fecha;
    private String hora;
    private String notas;
    private List<Long> adicionalIds;

    public Long getBarberoId() { return barberoId; }
    public void setBarberoId(Long barberoId) { this.barberoId = barberoId; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public List<Long> getAdicionalIds() { return adicionalIds; }
    public void setAdicionalIds(List<Long> adicionalIds) { this.adicionalIds = adicionalIds; }
}
