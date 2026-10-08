// Política de contraseñas del lado del cliente: da feedback inmediato antes de
// llamar al backend. La validación que de verdad protege los datos es la del
// servidor (com.jostech.emilker.util.PasswordPolicy); esta es solo para no
// hacerle perder tiempo al usuario mandando algo que sabemos que va a rebotar.
export function errorDePassword(password: string): string | null {
  if (!password || password.length < 8) {
    return 'La contraseña debe tener al menos 8 caracteres.';
  }
  const tieneLetra = /[A-Za-zÁÉÍÓÚáéíóúÑñ]/.test(password);
  const tieneNumero = /[0-9]/.test(password);
  if (!tieneLetra || !tieneNumero) {
    return 'La contraseña debe combinar letras y números.';
  }
  return null;
}
