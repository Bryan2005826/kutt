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
  if (err?.status === 0) {
    return 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo y que MySQL esté encendido.';
  }
  // si el backend SI respondio con un mensaje de texto (por ejemplo "Correo ya registrado"), lo usamos
  if (typeof err?.error === 'string' && err.error.trim()) {
    return err.error;
  }
  return mensajePorDefecto;
}
