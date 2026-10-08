package com.jostech.emilker.dto;

public class LoginRequest {
    private String correo;
    private String password;
    private String recaptchaToken; // NUEVO — token que Google genera al marcar el checkbox

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRecaptchaToken() { return recaptchaToken; }
    public void setRecaptchaToken(String recaptchaToken) { this.recaptchaToken = recaptchaToken; }
}
