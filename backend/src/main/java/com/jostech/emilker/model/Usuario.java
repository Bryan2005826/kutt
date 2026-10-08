package com.jostech.emilker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true)
    private String correo;

    private String passwordHash;

    // CLIENTE, ADMIN_NEGOCIO o SUPER_ADMIN (este \u00faltimo es el due\u00f1o de la plataforma Kutt)
    private String rol;

    // ---- CU: recuperar contrase\u00f1a olvidada ----
    // Token de un solo uso que se manda por correo cuando alguien pide restablecer
    // su contrase\u00f1a. Queda en null la mayor parte del tiempo; solo tiene valor
    // mientras hay una solicitud de restablecimiento pendiente y vigente.
    @Column(unique = true)
    private String resetToken;

    // Momento exacto en el que el token de arriba deja de ser v\u00e1lido (vida corta,
    // igual que cualquier link de "restablecer contrase\u00f1a" de un sistema real).
    private java.time.Instant resetTokenExpira;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public java.time.Instant getResetTokenExpira() { return resetTokenExpira; }
    public void setResetTokenExpira(java.time.Instant resetTokenExpira) { this.resetTokenExpira = resetTokenExpira; }
}
