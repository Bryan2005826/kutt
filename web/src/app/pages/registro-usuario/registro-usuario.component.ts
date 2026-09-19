import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-registro-usuario',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registro-usuario.component.html',
  styleUrl: './registro-usuario.component.css'
})
export class RegistroUsuarioComponent {
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

  cargando = signal(false);
  error = signal<string | null>(null);

  constructor(private auth: AuthService, private router: Router) {}

  crearCuenta() {
    if (!this.nombre || !this.apellidos || !this.correo || !this.password) {
      this.error.set('Completa al menos nombre, apellidos, correo y contraseña.');
      return;
    }
    this.error.set(null);
    this.cargando.set(true);

    this.auth.registro({
      nombre: this.nombre,
      correo: this.correo,
      password: this.password,
      rol: 'CLIENTE',
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
        this.router.navigate(['/agendar']);
      },
      error: err => {
        this.cargando.set(false);
        this.error.set(err?.error ?? 'No se pudo crear la cuenta.');
      }
    });
  }

  continuarConGoogle() {
    this.error.set('El acceso con Google se conectará próximamente. Regístrate con tu correo por ahora.');
  }

  continuarConFacebook() {
    this.error.set('El acceso con Facebook se conectará próximamente. Regístrate con tu correo por ahora.');
  }
}
