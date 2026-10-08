import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { mensajeDeError } from '../../utils/error.util';

// CU: recuperar contraseña olvidada — paso 1. El usuario escribe su correo y,
// si existe una cuenta con ese correo, le llega un enlace para restablecerla.
@Component({
  selector: 'app-olvide-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="kutt-reg-shell">
      <div class="kutt-reg-card">
        <a routerLink="/login" class="kutt-back">← Volver a iniciar sesión</a>
        <h2>¿Olvidaste tu contraseña?</h2>
        <p class="kutt-hint">Escribe el correo con el que te registraste y te enviaremos un enlace para crear una contraseña nueva.</p>

        <div class="kutt-field">
          <label>Correo electrónico</label>
          <input type="email" [(ngModel)]="correo" name="correo" placeholder="tucorreo@ejemplo.com" [disabled]="cargando() || enviado()">
        </div>

        <p class="kutt-error" *ngIf="error()">{{ error() }}</p>
        <p class="kutt-ok" *ngIf="enviado()">
          Si ese correo está registrado, te enviamos un enlace para restablecer tu contraseña. Revisa tu bandeja de entrada (y spam).
        </p>

        <button class="kutt-btn-primary" [disabled]="cargando() || enviado()" (click)="enviar()">
          {{ cargando() ? 'Enviando...' : 'Enviar enlace' }}
        </button>
      </div>
    </div>
  `,
  styleUrl: './olvide-password.component.css'
})
export class OlvidePasswordComponent {
  correo = '';
  cargando = signal(false);
  enviado = signal(false);
  error = signal<string | null>(null);

  constructor(private auth: AuthService) {}

  enviar() {
    if (!this.correo) {
      this.error.set('Escribe tu correo electrónico.');
      return;
    }
    this.error.set(null);
    this.cargando.set(true);

    this.auth.solicitarRecuperacion(this.correo).subscribe({
      next: () => {
        this.cargando.set(false);
        this.enviado.set(true);
      },
      error: err => {
        this.cargando.set(false);
        this.error.set(mensajeDeError(err, 'No se pudo enviar el enlace. Intenta de nuevo.'));
      }
    });
  }
}
