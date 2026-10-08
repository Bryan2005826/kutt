import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../../services/data.service';

@Component({
  selector: 'app-dashboard-clientes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <header class="content-header">
      <div>
        <p class="eyebrow">Clientes</p>
        <h1>{{ data.clientes().length }} clientes registrados</h1>
      </div>
    </header>

    <div class="panel">
      <table class="data-table">
        <thead>
          <tr><th>Nombre</th><th>Correo</th><th>Teléfono</th><th></th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let c of data.clientes()">
            <ng-container *ngIf="editando() === c.id; else vista">
              <td><input [(ngModel)]="nombreEdit" name="nombreEdit"></td>
              <td>{{ c.correo }}</td>
              <td><input [(ngModel)]="telefonoEdit" name="telefonoEdit"></td>
              <td class="actions-cell">
                <button class="row-action gold" (click)="guardar(c.id)">Guardar</button>
                <button class="row-action" (click)="cancelar()">Cancelar</button>
              </td>
            </ng-container>
            <ng-template #vista>
              <td>{{ c.nombre }}</td>
              <td>{{ c.correo }}</td>
              <td>{{ c.telefono }}</td>
              <td class="actions-cell">
                <button class="row-action" (click)="editar(c.id, c.nombre, c.telefono)">Editar</button>
              </td>
            </ng-template>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .content-header { margin-bottom: 26px; }
    .eyebrow { font-size: 13px; color: var(--pink); margin-bottom: 6px; }
    .content-header h1 { font-size: 22px; }
    .panel { background: var(--surface); border: 1px solid var(--surface-border); border-radius: var(--radius-md); padding: 8px 22px; }
    .actions-cell { display: flex; gap: 8px; justify-content: flex-end; }
    .row-action { background: var(--surface-2); color: var(--text-dim); border-radius: var(--radius-sm); padding: 7px 12px; font-size: 12px; }
    .row-action:hover { color: var(--text); }
    .row-action.gold { background: var(--pink-bright); color: #fff; font-weight: 600; }
    .data-table input {
      background: var(--surface-2); border: 1px solid var(--surface-border); border-radius: 6px;
      color: var(--text); padding: 6px 8px; font-size: 13px; width: 100%;
    }
  `]
})
export class ClientesComponent {
  editando = signal<number | null>(null);
  nombreEdit = '';
  telefonoEdit = '';

  constructor(public data: DataService) {}

  editar(id: number, nombre: string, telefono: string) {
    this.editando.set(id);
    this.nombreEdit = nombre;
    this.telefonoEdit = telefono;
  }

  cancelar() {
    this.editando.set(null);
  }

  guardar(id: number) {
    this.data.editarCliente(id, { nombre: this.nombreEdit, telefono: this.telefonoEdit });
    this.editando.set(null);
  }
}
