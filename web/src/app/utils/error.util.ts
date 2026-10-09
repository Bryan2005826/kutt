// Ayuda chiquita para sacar un mensaje de error que se pueda mostrar en pantalla.
//
// El problema que resuelve: cuando el navegador NO logra conectarse con el
// backend (por ejemplo, porque no esta corriendo), Angular no nos da el texto
// que devolvio el servidor -sencillamente porque el servidor nunca respondio-,
// sino un objeto tecnico (un ProgressEvent). Si a ese objeto lo mostramos tal
// cual en pantalla, sale el clasico "[object ProgressEvent]" que no le dice
// nada al usuario. Aqui lo detectamos y mostramos un mensaje humano en su lugar.
export function mensajeDeError(err: any, mensajePorDefecto: string): string {
  // status 0 = la peticion ni siquiera llego al servidor (sin conexion, backend apagado, CORS, etc.)
  // Antes este mensaje mencionaba "backend" y "MySQL" — lenguaje de desarrollador que no
  // le dice nada a un usuario real. Ahora es un mensaje humano y accionable.
  if (err?.status === 0) {
    return 'Ups, no pudimos conectar con Kutt en este momento. Revisa tu conexión a internet e inténtalo de nuevo en unos minutos.';
  }
  // Cualquier error 5xx es un problema de nuestro lado (configuración faltante, servicio
  // caído, etc.). Nunca mostramos el detalle tecnico que manda el backend en estos casos
  // (como "no está configurado en el servidor todavía") porque confunde al usuario; en su
  // lugar, un mensaje genérico y amable.
  if (typeof err?.status === 'number' && err.status >= 500) {
    return 'Ups, algo salió mal de nuestro lado. Inténtalo de nuevo en unos minutos.';
  }
  // si el backend SI respondio con un mensaje de texto pensado para el usuario
  // (por ejemplo "Correo ya registrado" o "Correo o contraseña incorrectos"), lo usamos
  if (typeof err?.error === 'string' && err.error.trim()) {
    return err.error;
  }
  return mensajePorDefecto;
}
