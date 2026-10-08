import { Component, EventEmitter, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { mensajeDeError } from '../../utils/error.util';

declare var grecaptcha: any;

// Modal ligero de inicio de sesion que se puede abrir encima de cualquier
// pagina (Descubrir, el perfil de un negocio, agendar cita, comprar
// productos) sin perder lo que la persona ya estaba haciendo: al iniciar
// sesion con exito, se emite (exito) y el componente que lo abrio continua
// la accion pendiente (dar like, confirmar la cita, pagar, etc.).
@Component({
  selector: 'app-login-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login-modal.component.html',
  styleUrl: './login-modal.component.css'
})
export class LoginModalComponent {
  @Output() exito = new EventEmitter<void>();
  @Output() cerrar = new EventEmitter<void>();

  correo = '';
  password = '';
  cargando = signal(false);
  error = signal<string | null>(null);

  constructor(private auth: AuthService) {}

  cerrarModal() {
    this.cerrar.emit();
  }

  entrar() {
    if (!this.correo || !this.password) {
      this.error.set('Ingresa tu correo y contraseña.');
      return;
    }

    const recaptchaToken = typeof grecaptcha !== 'undefined' ? grecaptcha.getResponse() : '';

    this.error.set(null);
    this.cargando.set(true);
    this.auth.login(this.correo, this.password, recaptchaToken).subscribe({
      next: () => {
        this.cargando.set(false);
        this.exito.emit();
      },
      error: err => {
        if (typeof grecaptcha !== 'undefined') grecaptcha.reset();
        this.cargando.set(false);
        this.error.set(mensajeDeError(err, 'Correo o contraseña incorrectos.'));
      }
    });
  }

  async continuarConGoogle() {
    this.error.set(null);
    this.cargando.set(true);
    try {
      await this.auth.loginConGoogle('CLIENTE');
      this.cargando.set(false);
      this.exito.emit();
    } catch (err: any) {
      this.cargando.set(false);
      this.error.set(mensajeDeError(err, 'No se pudo iniciar sesión con Google. Intenta de nuevo.'));
    }
  }

  async continuarConFacebook() {
    this.error.set(null);
    this.cargando.set(true);
    try {
      await this.auth.loginConFacebook('CLIENTE');
      this.cargando.set(false);
      this.exito.emit();
    } catch (err: any) {
      this.cargando.set(false);
      this.error.set(mensajeDeError(err, 'No se pudo iniciar sesión con Facebook. Intenta de nuevo.'));
    }
  }
}
