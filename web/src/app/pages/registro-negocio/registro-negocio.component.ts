import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-registro-negocio',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registro-negocio.component.html',
  styleUrl: './registro-negocio.component.css'
})
export class RegistroNegocioComponent {
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

  cargando = signal(false);
  error = signal<string | null>(null);

  constructor(private auth: AuthService, private router: Router) {}

  continuar() {
    if (!this.nombre || !this.correo || !this.password || !this.nombreNegocio) {
      this.error.set('Completa al menos tu nombre, correo, contraseña y el nombre del negocio.');
      return;
    }
    this.error.set(null);
    this.cargando.set(true);

    this.auth.registro({
      nombre: this.nombre,
      correo: this.correo,
      password: this.password,
      rol: 'ADMIN_NEGOCIO',
      nombreNegocio: this.nombreNegocio,
      rubro: this.rubro,
      telefonoFijoNegocio: this.telefonoFijoNegocio,
      departamentoNegocio: this.departamentoNegocio,
      direccionNegocio: this.direccionNegocio,
      logoUrl: this.logoUrl,
      portadaUrl: this.portadaUrl,
    }).subscribe({
      next: () => {
        this.cargando.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: err => {
        this.cargando.set(false);
        this.error.set(err?.error ?? 'No se pudo crear el negocio.');
      }
    });
  }
}
