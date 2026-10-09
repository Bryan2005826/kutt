package com.jostech.emilker.util;

import java.util.regex.Pattern;

// Política de contraseñas del sistema (CU-02).
// Se valida aquí, en el backend, porque la validación del frontend es solo
// para dar feedback rápido al usuario; la que de verdad protege los datos
// es esta, ya que el frontend se puede saltar llamando directo a la API.
public final class PasswordPolicy {

    // Al menos 8 caracteres, al menos una letra y al menos un número.
    // No exigimos símbolos para no volver la política tan estricta que la
    // gente termine anotando la contraseña en un papel.
    private static final Pattern TIENE_LETRA = Pattern.compile("[A-Za-zÁÉÍÓÚáéíóúÑñ]");
    private static final Pattern TIENE_NUMERO = Pattern.compile("[0-9]");

    private PasswordPolicy() {}

    public static boolean esValida(String password) {
        return mensajeDeError(password) == null;
    }

    // Devuelve null si la contraseña cumple la política, o el mensaje exacto
    // que hay que mostrarle al usuario si no la cumple.
    public static String mensajeDeError(String password) {
        if (password == null || password.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres.";
        }
        if (!TIENE_LETRA.matcher(password).find() || !TIENE_NUMERO.matcher(password).find()) {
            return "La contraseña debe combinar letras y números.";
        }
        return null;
    }
}
