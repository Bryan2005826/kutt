import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../../services/data.service';

@Component({
  selector: 'app-dashboard-ventas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <header class="content-header">
      <div>
        <p class="eyebrow">Ventas</p>
        <h1>{{ formatPrecio(total()) }} en ventas registradas</h1>
      </div>
      <button class="btn-gold" style="width:auto" (click)="mostrarForm.set(!mostrarForm())">
        {{ mostrarForm() ? 'Cancelar' : '+ Registrar venta' }}
      </button>
    </header>

    <div class="panel form-panel" *ngIf="mostrarForm()">
      <div class="form-row">
        <div class="field">
          <label>Cliente</label>
          <select [(ngModel)]="cliente" name="cliente">
            <option value="">Cliente ocasional</option>
            <option *ngFor="let c of data.clientes()" [value]="c.nombre">{{ c.nombre }}</option>
          </select>
        </div>
        <div class="field"><label>Detalle</label><input [(ngModel)]="detalle" name="detalle" placeholder="Ej: Shampoo Premium"></div>
        <div class="field"><label>Total</label><input type="number" [(ngModel)]="totalVenta" name="totalVenta"></div>
      </div>
      <button class="btn-gold" style="width:auto" (click)="guardar()">Guardar venta</button>
    </div>

    <div class="panel">
      <table class="data-table">
        <thead>
          <tr><th>Cliente</th><th>Detalle</th><th>Fecha</th><th>Total</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let v of data.ventas()">
            <td>{{ v.cliente }}</td>
            <td>{{ v.detalle }}</td>
            <td>{{ v.fecha }}</td>
            <td>{{ formatPrecio(v.total) }}</td>
          </tr>
        </tbody>
      </table>
      <p class="empty-hint" *ngIf="data.ventas().length === 0">
        Aún no hay ventas. Se registran al finalizar una cita o manualmente con el botón de arriba.
      </p>
    </div>
  `,
  styles: [`
    .content-header { display:flex; justify-content:space-between; align-items:flex-end; margin-bottom: 26px; }
    .eyebrow { font-size: 13px; color: var(--pink); margin-bottom: 6px; }
    .content-header h1 { font-size: 22px; }
    .panel { background: var(--surface); border: 1px solid var(--surface-border); border-radius: var(--radius-md); padding: 8px 22px; }
    .form-panel { padding: 22px; margin-bottom: 20px; }
    .form-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; }
    .empty-hint { font-size: 13px; color: var(--text-dim); padding: 20px 4px; }
    @media (max-width: 700px) {
      .content-header { flex-direction: column; align-items: flex-start; gap: 10px; }
      .form-row { grid-template-columns: 1fr; }
      .form-panel { padding: 16px; }
    }
  `]
})
export class VentasComponent {
  mostrarForm = signal(false);
  cliente = '';
  detalle = '';
  totalVenta = 0;

  constructor(public data: DataService) {}

  total = computed(() => this.data.ventas().reduce((sum, v) => sum + v.total, 0));

  guardar() {
    if (!this.detalle || this.totalVenta <= 0) return;
    const nombreCliente = this.cliente || 'Cliente ocasional';
    const fecha = new Date().toLocaleDateString('es-CO');
    this.data.registrarVenta(nombreCliente, this.detalle, this.totalVenta, fecha);
    this.cliente = ''; this.detalle = ''; this.totalVenta = 0;
    this.mostrarForm.set(false);
  }

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
