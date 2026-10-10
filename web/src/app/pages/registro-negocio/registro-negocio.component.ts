import { AfterViewInit, Component, ElementRef, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { DataService } from '../../services/data.service';
import { mensajeDeError } from '../../utils/error.util';
import { RecaptchaWidget } from '../../utils/recaptcha.util';
import { errorDePassword } from '../../utils/password.util';
import { TerminosCondicionesComponent } from '../../components/terminos-condiciones/terminos-condiciones.component';

@Component({
  selector: 'app-registro-negocio',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, TerminosCondicionesComponent],
  templateUrl: './registro-negocio.component.html',
  styleUrl: './registro-negocio.component.css'
})
export class RegistroNegocioComponent implements AfterViewInit, OnDestroy {
  private recaptcha = new RecaptchaWidget();
  nombre = '';
  correo = '';
  password = '';

  nombreNegocio = '';
  rubro = 'BARBERIA';
  telefonoFijoNegocio = '';
  departamentoNegocio = '';
  direccionNegocio = '';
  logoUrl = '';
  portadaUrl = '';
  aceptaTerminos = false;
  mostrarTerminos = signal(false);

  cargando = signal(false);
  error = signal<string | null>(null);
  private recaptchaToken = '';

  constructor(private auth: AuthService, private data: DataService, private router: Router, private host: ElementRef<HTMLElement>) {}

  ngAfterViewInit() {
    this.recaptcha.montar(this.host.nativeElement.querySelector('.g-recaptcha'));
  }

  ngOnDestroy() {
    this.recaptcha.destruir();
  }

  continuar() {
    if (!this.nombre || !this.correo || !this.password || !this.nombreNegocio) {
      this.error.set('Completa al menos tu nombre, correo, contraseña y el nombre del negocio.');
      return;
    }

    if (!this.aceptaTerminos) {
      this.error.set('Debes aceptar los términos y condiciones para registrar tu negocio.');
      return;
    }

    // Política de seguridad: contraseña mínima antes de siquiera llamar al backend.
    const errorPassword = errorDePassword(this.password);
    if (errorPassword) {
      this.error.set(errorPassword);
      return;
    }

    // Política de seguridad: reCAPTCHA también al registrar un negocio.
    this.recaptchaToken = this.recaptcha.respuesta();
    if (!this.recaptchaToken) {
      this.error.set('Por favor marca el reCAPTCHA antes de continuar.');
      return;
    }

    this.error.set(null);
    this.cargando.set(true);

    // Convertimos la dirección escrita a coordenadas reales antes de crear el
    // negocio, para que aparezca en el mapa de Descubrir en el sitio correcto
    // (y no en una ubicación inventada). Si no se puede geocodificar (dirección
    // muy genérica, sin conexión, etc.), seguimos igual sin coordenadas.
    this.data.geocodificarDireccion(this.direccionNegocio, this.departamentoNegocio).subscribe({
      next: resultados => {
        const primero = resultados?.[0];
        this.crearNegocio(primero ? Number(primero.lat) : undefined, primero ? Number(primero.lon) : undefined);
      },
      error: () => this.crearNegocio(undefined, undefined)
    });
  }

  private crearNegocio(latitudNegocio?: number, longitudNegocio?: number) {
    this.auth.registro({
      nombre: this.nombre,
      correo: this.correo,
      password: this.password,
      rol: 'ADMIN_NEGOCIO',
      recaptchaToken: this.recaptchaToken,
      nombreNegocio: this.nombreNegocio,
      rubro: this.rubro,
      telefonoFijoNegocio: this.telefonoFijoNegocio,
      departamentoNegocio: this.departamentoNegocio,
      direccionNegocio: this.direccionNegocio,
      logoUrl: this.logoUrl,
      portadaUrl: this.portadaUrl,
      latitudNegocio,
      longitudNegocio,
    }).subscribe({
      next: () => {
        this.cargando.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: err => {
        this.recaptcha.reiniciar();
        this.cargando.set(false);
        this.error.set(mensajeDeError(err, 'No se pudo crear el negocio.'));
      }
    });
  }
}
