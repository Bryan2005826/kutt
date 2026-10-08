package com.jostech.emilker.dto;

public class FirebaseLoginRequest {

    // El "carnet" que entrega Firebase despues de que la persona inicia sesion
    // con Google o Facebook en el navegador. El backend lo verifica antes de confiar en el.
    private String idToken;

    // Solo se usa si es la primera vez que esa persona entra a Kutt (para saber
    // si es Cliente o Admin de negocio); si la cuenta ya existe, se ignora.
    private String rol;

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
