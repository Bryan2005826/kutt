import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../../services/data.service';

@Component({
  selector: 'app-dashboard-inventario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <header class="content-header">
      <div>
        <p class="eyebrow">Inventario</p>
        <h1>Productos del establecimiento</h1>
      </div>
      <button class="btn-gold" style="width:auto" (click)="mostrarForm.set(!mostrarForm())">
        {{ mostrarForm() ? 'Cancelar' : '+ Agregar producto' }}
      </button>
    </header>

    <div class="panel form-panel" *ngIf="mostrarForm()">
      <div class="form-row">
        <div class="field"><label>Nombre</label><input [(ngModel)]="nombre" name="nombre"></div>
        <div class="field"><label>Precio</label><input type="number" [(ngModel)]="precio" name="precio"></div>
        <div class="field"><label>Existencias</label><input type="number" [(ngModel)]="existencias" name="existencias"></div>
        <div class="field"><label>Mínimo (alerta)</label><input type="number" [(ngModel)]="minimo" name="minimo"></div>
      </div>
      <button class="btn-gold" style="width:auto" (click)="guardar()">Guardar producto</button>
    </div>

    <div class="panel">
      <table class="data-table">
        <thead>
          <tr><th>Producto</th><th>Precio</th><th>Existencias</th><th>Estado</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let p of data.productos()">
            <td>{{ p.nombre }}</td>
            <td>{{ formatPrecio(p.precio) }}</td>
            <td>{{ p.existencias }}</td>
            <td>
              <span class="badge" [class.pending]="p.existencias <= p.minimo">
                {{ p.existencias <= p.minimo ? 'Bajo stock' : 'Disponible' }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .content-header { display:flex; justify-content:space-between; align-items:flex-end; margin-bottom: 26px; }
    .eyebrow { font-size: 13px; color: var(--gold); margin-bottom: 6px; }
    .content-header h1 { font-size: 22px; }
    .panel { background: var(--surface); border: 1px solid var(--surface-border); border-radius: var(--radius-md); padding: 8px 22px; }
    .form-panel { padding: 22px; margin-bottom: 20px; }
    .form-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; }
  `]
})
export class InventarioComponent {
  mostrarForm = signal(false);
  nombre = '';
  precio = 0;
  existencias = 0;
  minimo = 5;

  constructor(public data: DataService) {}

  guardar() {
    if (!this.nombre || this.precio <= 0) return;
    this.data.agregarProducto(this.nombre, this.precio, this.existencias, this.minimo);
    this.nombre = ''; this.precio = 0; this.existencias = 0; this.minimo = 5;
    this.mostrarForm.set(false);
  }

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
