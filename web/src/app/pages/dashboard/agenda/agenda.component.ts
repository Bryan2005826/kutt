import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataService } from '../../../services/data.service';

@Component({
  selector: 'app-dashboard-agenda',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './agenda.component.html',
  styleUrl: './agenda.component.css'
})
export class AgendaComponent {
  constructor(public data: DataService) {}

  aceptar(id: number) { this.data.confirmarCita(id); }
  rechazar(id: number) { this.data.rechazarCita(id); }
  finalizar(id: number) { this.data.finalizarCita(id); }

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
