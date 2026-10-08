import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

// Modal con el texto de términos y condiciones + política de tratamiento de
// datos. Se abre desde los formularios de registro (usuario y negocio) al
// hacer clic en el link de "términos y condiciones"; no acepta nada por sí
// mismo, solo informa — el checkbox que de verdad bloquea el registro vive
// en cada formulario.
@Component({
  selector: 'app-terminos-condiciones',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="modal-fondo" (click)="cerrarModal()">
      <div class="modal-caja" (click)="$event.stopPropagation()">
        <button class="modal-cerrar" (click)="cerrarModal()" aria-label="Cerrar">✕</button>
        <h2>Términos y condiciones</h2>
        <div class="kutt-terminos-texto">
          <p><strong>1. Qué es Kutt.</strong> Kutt es una plataforma que conecta a clientes con negocios de belleza y
          cuidado personal (barberías, salones de uñas, centros de estética, spas) para agendar citas, comprar
          productos y gestionar pagos.</p>

          <p><strong>2. Tu cuenta.</strong> Eres responsable de mantener la confidencialidad de tu contraseña y de
          toda la actividad que ocurra en tu cuenta. Debes darnos información real y mantenerla actualizada.</p>

          <p><strong>3. Uso del servicio.</strong> Te comprometes a usar Kutt de forma legal: no suplantar a otra
          persona, no intentar vulnerar la seguridad de la plataforma, y respetar las citas que agendas (cancela con
          anticipación si no vas a poder asistir).</p>

          <p><strong>4. Tratamiento de datos personales.</strong> Los datos que nos das (nombre, correo, teléfono,
          ubicación si la autorizas) se usan únicamente para operar el servicio: mostrarte negocios cercanos,
          gestionar tus citas y compras, y enviarte recordatorios. No vendemos tu información a terceros. Puedes
          pedir que corrijamos o eliminemos tus datos escribiéndonos.</p>

          <p><strong>5. Si eres dueño de un negocio.</strong> Al registrar tu negocio declaras que tienes autoridad
          para hacerlo y que la información del local (dirección, teléfono, horarios) es correcta. Eres responsable
          de mantener actualizado tu inventario, servicios y precios.</p>

          <p><strong>6. Cambios.</strong> Podemos actualizar estos términos; si lo hacemos de forma importante, te lo
          haremos saber dentro de la aplicación.</p>
        </div>
        <button class="kutt-terminos-cerrar" (click)="cerrarModal()">Entendido</button>
      </div>
    </div>
  `,
  styleUrl: './terminos-condiciones.component.css'
})
export class TerminosCondicionesComponent {
  @Output() cerrar = new EventEmitter<void>();

  cerrarModal() {
    this.cerrar.emit();
  }
}
