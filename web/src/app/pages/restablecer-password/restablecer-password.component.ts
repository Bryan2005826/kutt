import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { mensajeDeError } from '../../utils/error.util';

// CU: recuperar contraseña olvidada — paso 2. El usuario llega aquí desde el
// enlace que le mandamos por correo (?token=...) y define su nueva contraseña.
@Component({
  selector: 'app-restablecer-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="kutt-reg-shell">
      <div class="kutt-reg-card">
        <a routerLink="/login" class="kutt-back">← Volver a iniciar sesión</a>
        <h2>Crea una nueva contraseña</h2>

        <ng-container *ngIf="!tokenValido()">
          <p class="kutt-error">Este enlace no es válido o le falta el token. Pide uno nuevo desde "¿Olvidaste tu contraseña?".</p>
        </ng-container>

        <ng-container *ngIf="tokenValido() && !listo()">
          <div class="kutt-field">
            <label>Nueva contraseña</label>
            <input type="password" [(ngModel)]="password" name="password" placeholder="••••••••" [disabled]="cargando()">
          </div>
          <div class="kutt-field">
            <label>Confirma la nueva contraseña</label>
            <input type="password" [(ngModel)]="confirmar" name="confirmar" placeholder="••••••••" [disabled]="cargando()">
          </div>
          <p class="kutt-hint">Mínimo 8 caracteres, combinando letras y números.</p>

          <p class="kutt-error" *ngIf="error()">{{ error() }}</p>

          <button class="kutt-btn-primary" [disabled]="cargando()" (click)="guardar()">
            {{ cargando() ? 'Guardando...' : 'Guardar nueva contraseña' }}
          </button>
        </ng-container>

        <ng-container *ngIf="listo()">
          <p class="kutt-ok">Tu contraseña se actualizó correctamente.</p>
          <a routerLink="/login" class="kutt-btn-primary kutt-btn-link">Ir a iniciar sesión</a>
        </ng-container>
      </div>
    </div>
  `,
  styleUrl: './restablecer-password.component.css'
})
export class RestablecerPasswordComponent implements OnInit {
  password = '';
  confirmar = '';
  token = '';

  cargando = signal(false);
  listo = signal(false);
  error = signal<string | null>(null);
  tokenValido = signal(true);

  constructor(private auth: AuthService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';
    this.tokenValido.set(!!this.token);
  }

  guardar() {
    if (!this.password || this.password.length < 8) {
      this.error.set('La contraseña debe tener al menos 8 caracteres.');
      return;
    }
    if (this.password !== this.confirmar) {
      this.error.set('Las dos contraseñas no coinciden.');
      return;
    }
    this.error.set(null);
    this.cargando.set(true);

    this.auth.restablecerPassword(this.token, this.password).subscribe({
      next: () => {
        this.cargando.set(false);
        this.listo.set(true);
      },
      error: err => {
        this.cargando.set(false);
        this.error.set(mensajeDeError(err, 'No se pudo restablecer la contraseña. El enlace pudo haber expirado.'));
      }
    });
  }
}
