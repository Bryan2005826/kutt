import { AfterViewInit, Component, ElementRef, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { mensajeDeError } from '../../utils/error.util';
import { RecaptchaWidget } from '../../utils/recaptcha.util';
import { errorDePassword } from '../../utils/password.util';
import { TerminosCondicionesComponent } from '../../components/terminos-condiciones/terminos-condiciones.component';

@Component({
  selector: 'app-registro-usuario',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, TerminosCondicionesComponent],
  templateUrl: './registro-usuario.component.html',
  styleUrl: './registro-usuario.component.css'
})
export class RegistroUsuarioComponent implements AfterViewInit, OnDestroy {
  private recaptcha = new RecaptchaWidget();
  nombre = '';
  apellidos = '';
  correo = '';
  password = '';
  telefono = '';
  departamento = '';
  ciudad = '';
  genero: 'Masculino' | 'Femenino' | 'Otro' = 'Masculino';
  fechaNacimiento = '';
  direccion = '';
  permiteUbicacion = false;
  aceptaTerminos = false;
  mostrarTerminos = signal(false);

  cargando = signal(false);
  error = signal<string | null>(null);

  constructor(private auth: AuthService, private router: Router, private host: ElementRef<HTMLElement>) {}

  ngAfterViewInit() {
    this.recaptcha.montar(this.host.nativeElement.querySelector('.g-recaptcha'));
  }

  ngOnDestroy() {
    this.recaptcha.destruir();
  }

  crearCuenta() {
    if (!this.nombre || !this.apellidos || !this.correo || !this.password) {
      this.error.set('Completa al menos nombre, apellidos, correo y contraseña.');
      return;
    }

    if (!this.aceptaTerminos) {
      this.error.set('Debes aceptar los términos y condiciones para crear tu cuenta.');
      return;
    }

    // Política de seguridad: contraseña mínima antes de siquiera llamar al backend.
    const errorPassword = errorDePassword(this.password);
    if (errorPassword) {
      this.error.set(errorPassword);
      return;
    }

    // Política de seguridad: reCAPTCHA también al registrarse, igual que en el login.
    const recaptchaToken = this.recaptcha.respuesta();
    if (!recaptchaToken) {
      this.error.set('Por favor marca el reCAPTCHA antes de continuar.');
      return;
    }

    this.error.set(null);
    this.cargando.set(true);

    this.auth.registro({
      nombre: this.nombre,
      correo: this.correo,
      password: this.password,
      rol: 'CLIENTE',
      recaptchaToken,
      apellidos: this.apellidos,
      telefono: this.telefono,
      departamento: this.departamento,
      ciudad: this.ciudad,
      genero: this.genero,
      fechaNacimiento: this.fechaNacimiento,
      direccion: this.direccion,
      permiteUbicacion: this.permiteUbicacion,
    }).subscribe({
      next: () => {
        this.cargando.set(false);
        this.router.navigate(['/descubrir']);
      },
      error: err => {
        this.recaptcha.reiniciar();
        this.cargando.set(false);
        this.error.set(mensajeDeError(err, 'No se pudo crear la cuenta.'));
      }
    });
  }
}
