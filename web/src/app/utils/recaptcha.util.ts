// Maneja el widget de reCAPTCHA de cada pagina.
//
// Problema que resuelve: el script de Google dibuja solo los cuadros "g-recaptcha" que
// ya existen en la pagina cuando termina de cargar. En una app Angular, el formulario de
// login aparece DESPUES (al navegar entre paginas), asi que muchas veces el cuadro nunca
// se dibujaba y, al pulsar "Entrar", grecaptcha.getResponse() lanzaba un error y el boton
// parecia no hacer nada. Aqui dibujamos el widget nosotros, cuando la pagina ya lo tiene,
// y guardamos su id para leer/reiniciar justo ese widget.
const SITE_KEY = '6LeradAtAAAAAODAjjHylub4EuGnF9SvR1k6R4kN';

export class RecaptchaWidget {
  private id: number | null = null;
  private temporizador?: ReturnType<typeof setTimeout>;

  // Dibuja el widget dentro de `contenedor`; si el script de Google aun no cargo, reintenta.
  montar(contenedor: HTMLElement | null, intentos = 80): void {
    if (!contenedor || this.id !== null) return;
    const g = (window as any).grecaptcha;
    if (g && typeof g.ready === 'function') {
      g.ready(() => {
        if (this.id !== null) return;
        try {
          this.id = g.render(contenedor, { sitekey: SITE_KEY });
        } catch {
          /* ya estaba dibujado: no pasa nada */
        }
      });
    } else if (intentos > 0) {
      this.temporizador = setTimeout(() => this.montar(contenedor, intentos - 1), 250);
    }
  }

  // Token que dejo el usuario al marcar la casilla; '' si no la marco o no cargo.
  respuesta(): string {
    try {
      return this.id !== null ? ((window as any).grecaptcha.getResponse(this.id) || '') : '';
    } catch {
      return '';
    }
  }

  reiniciar(): void {
    try {
      if (this.id !== null) (window as any).grecaptcha.reset(this.id);
    } catch { /* nada */ }
  }

  destruir(): void {
    if (this.temporizador) clearTimeout(this.temporizador);
  }
}
