import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  mostrarRoles = signal(false);
  cargando = signal(false);
  error = signal<string | null>(null);

  correo = '';
  password = '';

  constructor(private auth: AuthService, private router: Router) {}

  toggleRoles() {
    this.mostrarRoles.update(v => !v);
    this.error.set(null);
  }

  entrar() {
    if (!this.correo || !this.password) {
      this.error.set('Ingresa tu correo y contraseña.');
      return;
    }
    this.error.set(null);
    this.cargando.set(true);

    this.auth.login(this.correo, this.password).subscribe({
      next: res => {
        this.cargando.set(false);
        this.router.navigate([res.rol === 'ADMIN_NEGOCIO' ? '/dashboard' : '/agendar']);
      },
      error: err => {
        this.cargando.set(false);
        this.error.set(err?.error ?? 'Correo o contraseña incorrectos.');
      }
    });
  }

  continuarConGoogle() {
    this.error.set('El acceso con Google se conectará próximamente. Usa correo y contraseña por ahora.');
  }

  continuarConFacebook() {
    this.error.set('El acceso con Facebook se conectará próximamente. Usa correo y contraseña por ahora.');
  }
}
