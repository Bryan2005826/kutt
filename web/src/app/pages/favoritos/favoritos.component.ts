import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DataService } from '../../services/data.service';
import { AuthService } from '../../services/auth.service';

const NOMBRES_RUBRO: Record<string, string> = {
  BARBERIA: 'Barbería', UNAS: 'Uñas', ESTETICA: 'Estética', SPA: 'Spa', OTRO: 'Otro',
};

@Component({
  selector: 'app-favoritos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './favoritos.component.html',
  styleUrl: './favoritos.component.css'
})
export class FavoritosComponent {
  constructor(public data: DataService, public auth: AuthService) {
    const correo = this.auth.usuario()?.correo;
    if (correo) this.data.cargarFavoritos(correo);
  }

  negociosFavoritos = computed(() => {
    const ids = new Set(this.data.favoritos().map(f => f.negocioId));
    return this.data.negocios().filter(n => ids.has(n.id));
  });

  nombreRubro(r: string): string {
    return NOMBRES_RUBRO[r] ?? r;
  }

  quitar(negocioId: number, evento: Event) {
    evento.stopPropagation();
    evento.preventDefault();
    const correo = this.auth.usuario()?.correo;
    if (correo) this.data.alternarFavorito(correo, negocioId);
  }
}
