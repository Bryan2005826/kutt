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
      <div class="field" style="max-width:280px;">
        <label>Foto (elige una de tu galería)</label>
        <input type="file" accept="image/*" (change)="elegirFoto($event)">
        <span class="subiendo-aviso" *ngIf="subiendoFoto()">Subiendo foto...</span>
        <img *ngIf="fotoUrl" [src]="fotoUrl" alt="" class="foto-preview">
      </div>
      <button class="btn-gold" style="width:auto" (click)="guardar()">Guardar producto</button>
    </div>

    <div class="panel">
      <table class="data-table">
        <thead>
          <tr><th></th><th>Producto</th><th>Precio</th><th>Existencias</th><th>Estado</th><th></th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let p of data.productos()">
            <ng-container *ngIf="editando() === p.id; else vista">
              <td><img class="foto-mini" [src]="fotoEdit || 'https://picsum.photos/seed/producto-generico/80/80'" alt=""></td>
              <td><input [(ngModel)]="nombreEdit" name="nombreEdit"></td>
              <td><input type="number" [(ngModel)]="precioEdit" name="precioEdit" style="width:90px"></td>
              <td><input type="number" [(ngModel)]="existenciasEdit" name="existenciasEdit" style="width:70px"></td>
              <td>
                <input type="file" accept="image/*" (change)="elegirFotoEdit($event)">
                <span class="subiendo-aviso" *ngIf="subiendoFotoEdit()">Subiendo...</span>
              </td>
              <td class="actions-cell">
                <button class="row-action gold" (click)="guardar2(p.id)">Guardar</button>
                <button class="row-action" (click)="cancelar()">Cancelar</button>
              </td>
            </ng-container>
            <ng-template #vista>
              <td><img class="foto-mini" [src]="p.fotoUrl || 'https://picsum.photos/seed/producto-generico/80/80'" [alt]="p.nombre"></td>
              <td>{{ p.nombre }}</td>
              <td>{{ formatPrecio(p.precio) }}</td>
              <td>{{ p.existencias }}</td>
              <td>
                <span class="badge" [class.pending]="p.existencias <= p.minimo">
                  {{ p.existencias <= p.minimo ? 'Bajo stock' : 'Disponible' }}
                </span>
              </td>
              <td class="actions-cell">
                <button class="row-action" (click)="editar(p)">Editar</button>
                <button class="row-action danger" (click)="eliminar(p)">Eliminar</button>
              </td>
            </ng-template>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .content-header { display:flex; justify-content:space-between; align-items:flex-end; margin-bottom: 26px; }
    .eyebrow { font-size: 13px; color: var(--pink); margin-bottom: 6px; }
    .content-header h1 { font-size: 22px; }
    .panel { background: var(--surface); border: 1px solid var(--surface-border); border-radius: var(--radius-md); padding: 8px 22px; }
    .form-panel { padding: 22px; margin-bottom: 20px; }
    .form-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 14px; }
    .foto-mini { width: 40px; height: 40px; border-radius: 8px; object-fit: cover; display: block; }
    .foto-preview { width: 60px; height: 60px; border-radius: 8px; object-fit: cover; margin-top: 8px; }
    .subiendo-aviso { font-size: 11.5px; color: var(--pink-bright); display: block; margin-top: 4px; }
    .actions-cell { display: flex; gap: 8px; justify-content: flex-end; }
    .row-action { background: var(--surface-2); color: var(--text-dim); border-radius: var(--radius-sm); padding: 7px 12px; font-size: 12px; }
    .row-action:hover { color: var(--text); }
    .row-action.gold { background: var(--pink-bright); color: #fff; font-weight: 600; }
    .row-action.danger { color: #e05d5d; }
    .row-action.danger:hover { color: #ff7a7a; }
    .data-table input {
      background: var(--surface-2); border: 1px solid var(--surface-border); border-radius: 6px;
      color: var(--text); padding: 6px 8px; font-size: 13px; width: 100%;
    }
    @media (max-width: 700px) {
      .content-header { flex-direction: column; align-items: flex-start; gap: 10px; }
      .form-row { grid-template-columns: 1fr 1fr; }
      .form-panel { padding: 16px; }
    }
  `]
})
export class InventarioComponent {
  mostrarForm = signal(false);
  nombre = '';
  precio = 0;
  existencias = 0;
  minimo = 5;
  fotoUrl = '';
  subiendoFoto = signal(false);

  // esto es lo que se usa mientras se edita una fila que ya existe
  editando = signal<number | null>(null);
  nombreEdit = '';
  precioEdit = 0;
  existenciasEdit = 0;
  fotoEdit = '';
  subiendoFotoEdit = signal(false);

  constructor(public data: DataService) {}

  // se dispara al elegir una foto en el formulario de "Agregar producto"
  elegirFoto(evento: Event) {
    const archivo = (evento.target as HTMLInputElement).files?.[0];
    if (!archivo) return;
    this.subiendoFoto.set(true);
    this.data.subirFoto(
      archivo,
      url => { this.fotoUrl = url; this.subiendoFoto.set(false); },
      () => this.subiendoFoto.set(false)
    );
  }

  // lo mismo pero para cuando se esta editando una fila que ya existe
  elegirFotoEdit(evento: Event) {
    const archivo = (evento.target as HTMLInputElement).files?.[0];
    if (!archivo) return;
    this.subiendoFotoEdit.set(true);
    this.data.subirFoto(
      archivo,
      url => { this.fotoEdit = url; this.subiendoFotoEdit.set(false); },
      () => this.subiendoFotoEdit.set(false)
    );
  }

  guardar() {
    if (!this.nombre || this.precio <= 0) return;
    this.data.agregarProducto(this.nombre, this.precio, this.existencias, this.minimo, this.fotoUrl);
    this.nombre = ''; this.precio = 0; this.existencias = 0; this.minimo = 5; this.fotoUrl = '';
    this.mostrarForm.set(false);
  }

  // ojo: esta funcion se llama editar() para abrir el modo edicion de una fila,
  // no confundir con guardar2() que es la que de verdad manda el cambio al backend
  editar(p: { id: number; nombre: string; precio: number; existencias: number; fotoUrl?: string }) {
    this.editando.set(p.id);
    this.nombreEdit = p.nombre;
    this.precioEdit = p.precio;
    this.existenciasEdit = p.existencias;
    this.fotoEdit = p.fotoUrl ?? '';
  }

  cancelar() {
    this.editando.set(null);
  }

  eliminando = signal<number | null>(null);

  eliminar(p: { id: number; nombre: string }) {
    const confirmado = window.confirm(`¿Eliminar "${p.nombre}" del inventario? Esta acción no se puede deshacer.`);
    if (!confirmado) return;
    this.eliminando.set(p.id);
    this.data.eliminarProducto(
      p.id,
      () => this.eliminando.set(null),
      () => this.eliminando.set(null)
    );
  }

  guardar2(id: number) {
    const producto = this.data.productos().find(p => p.id === id);
    this.data.editarProducto(id, {
      nombre: this.nombreEdit,
      precio: this.precioEdit,
      existencias: this.existenciasEdit,
      minimo: producto?.minimo ?? 5,
      fotoUrl: this.fotoEdit,
    });
    this.editando.set(null);
  }

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
