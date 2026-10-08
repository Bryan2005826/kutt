import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DataService } from '../../services/data.service';
import { AuthService } from '../../services/auth.service';
import { LoginModalComponent } from '../../components/login-modal/login-modal.component';

const NOMBRES_RUBRO: Record<string, string> = {
  BARBERIA: 'Barbería', UNAS: 'Manicura', ESTETICA: 'Centro de estética', SPA: 'Spa', OTRO: 'Otro',
};

@Component({
  selector: 'app-negocio-perfil',
  standalone: true,
  imports: [CommonModule, RouterLink, LoginModalComponent],
  templateUrl: './negocio-perfil.component.html',
  styleUrl: './negocio-perfil.component.css'
})
export class NegocioPerfilComponent {
  negocioId: number;

  mostrarLoginModal = signal(false);
  private accionPendiente: (() => void) | null = null;

  constructor(private route: ActivatedRoute, public data: DataService, public auth: AuthService) {
    this.negocioId = Number(this.route.snapshot.paramMap.get('id'));
    const correo = this.auth.usuario()?.correo;
    if (correo) this.data.cargarFavoritos(correo);

    // Multi-tenant: el equipo y los adicionales son propios de ESTE negocio
    this.data.cargarBarberos(this.negocioId);
    this.data.cargarAdicionales(this.negocioId);
  }

  negocio = computed(() => this.data.negocios().find(n => n.id === this.negocioId));
  resenas = computed(() => this.data.resenasDe(this.negocioId));
  promedio = computed(() => this.data.promedioCalificacion(this.negocioId));

  nombreRubro(r?: string): string {
    return r ? (NOMBRES_RUBRO[r] ?? r) : '';
  }

  toggleFavorito() {
    this.ejecutarConSesion(() => {
      const correo = this.auth.usuario()?.correo;
      if (correo) this.data.alternarFavorito(correo, this.negocioId);
    });
  }

  private ejecutarConSesion(accion: () => void) {
    if (this.auth.usuario()) { accion(); return; }
    this.accionPendiente = accion;
    this.mostrarLoginModal.set(true);
  }

  loginExitoso() {
    this.mostrarLoginModal.set(false);
    const correo = this.auth.usuario()?.correo;
    if (correo) this.data.cargarFavoritos(correo);
    if (this.accionPendiente) {
      const accion = this.accionPendiente;
      this.accionPendiente = null;
      accion();
    }
  }

  cerrarLoginModal() {
    this.mostrarLoginModal.set(false);
    this.accionPendiente = null;
  }

  esFavorito(): boolean {
    return this.data.esFavorito(this.negocioId);
  }

  estrellas(cal: number): number[] {
    return [1, 2, 3, 4, 5].map(n => (n <= Math.round(cal) ? 1 : 0));
  }

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
