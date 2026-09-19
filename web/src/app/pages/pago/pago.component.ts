import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DataService, Cita, Venta } from '../../services/data.service';

type Item = (Cita | Venta) & { esVenta?: boolean };

@Component({
  selector: 'app-pago',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './pago.component.html',
  styleUrl: './pago.component.css'
})
export class PagoComponent {
  tipo: 'cita' | 'venta';
  id: number;
  metodoSeleccionado = signal<number | null>(null);
  procesando = signal(false);
  resultado = signal<Cita | Venta | null>(null);

  constructor(private route: ActivatedRoute, public data: DataService) {
    this.tipo = this.route.snapshot.paramMap.get('tipo') === 'venta' ? 'venta' : 'cita';
    this.id = Number(this.route.snapshot.paramMap.get('id'));
  }

  item = computed<Item | undefined>(() => {
    if (this.tipo === 'cita') {
      return this.data.citas().find(c => c.id === this.id);
    }
    return this.data.ventas().find(v => v.id === this.id);
  });

  esCita(item: Item): item is Cita {
    return this.tipo === 'cita';
  }

  elegirMetodo(id: number) {
    this.metodoSeleccionado.set(id);
  }

  confirmarPago() {
    const metodoId = this.metodoSeleccionado();
    if (metodoId === null) return;
    this.procesando.set(true);

    if (this.tipo === 'cita') {
      this.data.pagarCita(this.id, metodoId, actualizada => {
        this.procesando.set(false);
        this.resultado.set(actualizada);
      });
    } else {
      this.data.pagarVenta(this.id, metodoId, actualizada => {
        this.procesando.set(false);
        this.resultado.set(actualizada);
      });
    }
  }

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
