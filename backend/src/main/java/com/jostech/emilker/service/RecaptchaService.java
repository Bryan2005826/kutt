package com.jostech.emilker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

// Verifica el token de reCAPTCHA que manda el frontend contra la API de Google,
// para confirmar que quien inicia sesion es una persona real y no un bot.
@Service
public class RecaptchaService {

    @Value("${app.recaptcha.secret:}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean esValido(String token) {
        if (secretKey == null || secretKey.isBlank()) {
            // si no hay llave configurada, no bloqueamos el login (modo desarrollo)
            return true;
        }
        if (token == null || token.isBlank()) {
            return false;
        }
        String url = "https://www.google.com/recaptcha/api/siteverify"
                + "?secret=" + secretKey + "&response=" + token;
        try {
            Map<?, ?> respuesta = restTemplate.postForObject(url, null, Map.class);
            return respuesta != null && Boolean.TRUE.equals(respuesta.get("success"));
        } catch (Exception e) {
            // si Google no responde (sin internet, etc.), no tumbamos el login
            // por un problema externo; lo dejamos pasar para no bloquear la app
            return true;
        }
    }
}
