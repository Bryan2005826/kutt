import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DataService } from '../../../services/data.service';

@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.css'
})
export class DashboardHomeComponent {
  constructor(public data: DataService) {}

  citasActivas = computed(() => this.data.citas().filter(c => c.estado !== 'Cancelada'));
  ventasHoy = computed(() => this.data.ventas().reduce((sum, v) => sum + v.total, 0));
  bajoStock = computed(() => this.data.productos().filter(p => p.existencias <= p.minimo));
  clientesTotal = computed(() => this.data.clientes().length);

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
