import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DataService, Cita, Venta } from '../../services/data.service';

// Pagina publica que se abre cuando alguien escanea el QR de un pago/ticket.
// Antes el QR solo guardaba un texto ("KUTT-VENTA-2-22000-Nequi") y el celular
// lo trataba como una busqueda en Google. Ahora el QR guarda el link a esta
// pagina, que muestra el comprobante ya armado (negocio, detalle, total, estado).
@Component({
  selector: 'app-ticket',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './ticket.component.html',
  styleUrl: './ticket.component.css'
})
export class TicketComponent {
  tipo: 'cita' | 'venta';
  id: number;
  buscando = signal(true);

  constructor(private route: ActivatedRoute, public data: DataService) {
    this.tipo = this.route.snapshot.paramMap.get('tipo') === 'venta' ? 'venta' : 'cita';
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    // si en unos segundos no aparece, mostramos "no encontrado" en vez de cargar para siempre
    setTimeout(() => this.buscando.set(false), 5000);
  }

  item = computed<Cita | Venta | undefined>(() =>
    this.tipo === 'cita'
      ? this.data.citas().find(c => c.id === this.id)
      : this.data.ventas().find(v => v.id === this.id)
  );

  cita = computed(() => (this.tipo === 'cita' ? (this.item() as Cita | undefined) : undefined));
  venta = computed(() => (this.tipo === 'venta' ? (this.item() as Venta | undefined) : undefined));

  negocio = computed(() => {
    const negocioId = this.item()?.negocioId;
    return negocioId != null ? this.data.negocios().find(n => n.id === negocioId) : undefined;
  });

  pagado = computed(() => this.item()?.estadoPago === 'Pagado');

  referencia = computed(() => `KUTT-${this.tipo === 'cita' ? 'CITA' : 'VENTA'}-${this.id}`);

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
