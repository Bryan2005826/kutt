import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataService, Negocio, ResumenPlataforma } from '../../services/data.service';
import { AuthService } from '../../services/auth.service';

const NOMBRES_RUBRO: Record<string, string> = {
  BARBERIA: 'Barbería', UNAS: 'Uñas', ESTETICA: 'Estética', SPA: 'Spa', OTRO: 'Otro',
};

@Component({
  selector: 'app-plataforma',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './plataforma.component.html',
  styleUrl: './plataforma.component.css'
})
export class PlataformaComponent {
  resumen = signal<ResumenPlataforma | null>(null);

  constructor(public data: DataService, public auth: AuthService) {
    this.data.cargarResumenPlataforma(r => this.resumen.set(r));
  }

  filasPorRubro = computed(() => {
    const r = this.resumen();
    if (!r) return [];
    const entradas = Object.entries(r.negociosPorRubro);
    const max = Math.max(1, ...entradas.map(([, v]) => v));
    return entradas.map(([rubro, cantidad]) => ({
      rubro: NOMBRES_RUBRO[rubro] ?? rubro,
      cantidad,
      porcentaje: Math.round((cantidad / max) * 100),
    }));
  });

  negocios = computed<Negocio[]>(() => this.resumen()?.negocios ?? []);

  nombreRubro(r: string): string {
    return NOMBRES_RUBRO[r] ?? r;
  }
}
