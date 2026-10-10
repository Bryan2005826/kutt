import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { mensajeDeError } from '../../utils/error.util';
import { RecaptchaWidget } from '../../utils/recaptcha.util';

interface SlideCarrusel {
  imagen: string;
  titulo: string;
  detalle: string;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit, AfterViewInit, OnDestroy {
  private recaptcha = new RecaptchaWidget();
  mostrarRoles = signal(false);
  cargando = signal(false);
  error = signal<string | null>(null);

  correo = '';
  password = '';

  // Fotos reales del negocio, por rubro
  slides: SlideCarrusel[] = [
    { imagen: 'assets/carousel/barberia.jpg', titulo: 'Barbería', detalle: 'Agenda tu corte con el barbero de tu preferencia' },
    { imagen: 'assets/carousel/unas.jpg', titulo: 'Uñas', detalle: 'Reserva tu manicure en el salón que más te guste' },
    { imagen: 'assets/carousel/estetica.jpg', titulo: 'Estética', detalle: 'Encuentra centros de estética cerca de ti' },
  ];

  slideActual = signal(0);
  private intervalo?: ReturnType<typeof setInterval>;

  constructor(private auth: AuthService, private router: Router, private host: ElementRef<HTMLElement>) {}

  ngOnInit() {
    this.intervalo = setInterval(() => this.siguienteSlide(), 4500);
  }

  ngAfterViewInit() {
    this.recaptcha.montar(this.host.nativeElement.querySelector('.g-recaptcha'));
  }

  ngOnDestroy() {
    this.recaptcha.destruir();
    if (this.intervalo) clearInterval(this.intervalo);
  }

  // Botón "← Volver" arriba de todo. Antes usaba location.back(), pero eso
  // causaba un ping-pong con las páginas de registro (que siempre regresan a
  // /login): login -> registro -> "Volver" -> login -> "Volver" -> de nuevo
  // registro, en bucle. Para evitarlo, "Volver" en el login siempre manda a
  // un destino fijo (Descubrir), sin depender del historial del navegador.
  volver() {
    this.router.navigate(['/descubrir']);
  }

  siguienteSlide() {
    this.slideActual.update(i => (i + 1) % this.slides.length);
  }

  irASlide(i: number) {
    this.slideActual.set(i);
  }

  toggleRoles() {
    this.mostrarRoles.update(v => !v);
    this.error.set(null);
  }

  entrar() {
    if (!this.correo || !this.password) {
      this.error.set('Ingresa tu correo y contraseña.');
      return;
    }

    // NUEVO: validar reCAPTCHA antes de llamar al backend
    const recaptchaToken = this.recaptcha.respuesta();
    if (!recaptchaToken) {
      this.error.set('Por favor marca el reCAPTCHA antes de continuar.');
      return;
    }

    this.error.set(null);
    this.cargando.set(true);

    this.auth.login(this.correo, this.password, recaptchaToken).subscribe({
      next: res => {
        this.cargando.set(false);
        const destino = res.rol === 'ADMIN_NEGOCIO' ? '/dashboard' : res.rol === 'SUPER_ADMIN' ? '/plataforma' : '/descubrir';
        this.router.navigate([destino]);
      },
      error: err => {
        this.recaptcha.reiniciar();
        this.cargando.set(false);
        this.error.set(mensajeDeError(err, 'Correo o contraseña incorrectos.'));
      }
    });
  }

  async continuarConGoogle() {
    this.error.set(null);
    this.cargando.set(true);
    try {
      const res = await this.auth.loginConGoogle('CLIENTE');
      const destino = res.rol === 'ADMIN_NEGOCIO' ? '/dashboard' : res.rol === 'SUPER_ADMIN' ? '/plataforma' : '/descubrir';
      this.router.navigate([destino]);
    } catch (err: any) {
      this.error.set(mensajeDeError(err, 'No se pudo iniciar sesión con Google. Intenta de nuevo.'));
    } finally {
      this.cargando.set(false);
    }
  }

  async continuarConFacebook() {
    this.error.set(null);
    this.cargando.set(true);
    try {
      const res = await this.auth.loginConFacebook('CLIENTE');
      const destino = res.rol === 'ADMIN_NEGOCIO' ? '/dashboard' : res.rol === 'SUPER_ADMIN' ? '/plataforma' : '/descubrir';
      this.router.navigate([destino]);
    } catch (err: any) {
      this.error.set(mensajeDeError(err, 'No se pudo iniciar sesión con Facebook. Intenta de nuevo.'));
    } finally {
      this.cargando.set(false);
    }
  }
}