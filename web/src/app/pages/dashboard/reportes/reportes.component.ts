import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataService } from '../../../services/data.service';

@Component({
  selector: 'app-dashboard-reportes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reportes.component.html',
  styleUrl: './reportes.component.css'
})
export class ReportesComponent {
  constructor(public data: DataService) {}

  // ---- Ventas ----
  totalVentas = computed(() => this.data.ventas().reduce((sum, v) => sum + v.total, 0));
  promedioVenta = computed(() => {
    const n = this.data.ventas().length;
    return n === 0 ? 0 : this.totalVentas() / n;
  });

  // ---- Citas por estado ----
  citasPorEstado = computed(() => {
    const estados: Array<'Pendiente' | 'Confirmada' | 'Finalizada' | 'Cancelada'> =
      ['Pendiente', 'Confirmada', 'Finalizada', 'Cancelada'];
    const citas = this.data.citas();
    const conteo = estados.map(estado => ({
      estado,
      cantidad: citas.filter(c => c.estado === estado).length
    }));
    const max = Math.max(1, ...conteo.map(c => c.cantidad));
    return conteo.map(c => ({ ...c, porcentaje: Math.round((c.cantidad / max) * 100) }));
  });

  // ---- Servicios más agendados ----
  serviciosPopulares = computed(() => {
    const citas = this.data.citas();
    const conteoMap = new Map<string, number>();
    citas.forEach(c => conteoMap.set(c.servicio, (conteoMap.get(c.servicio) ?? 0) + 1));
    const lista = Array.from(conteoMap.entries()).map(([servicio, cantidad]) => ({ servicio, cantidad }));
    lista.sort((a, b) => b.cantidad - a.cantidad);
    const max = Math.max(1, ...lista.map(l => l.cantidad));
    return lista.map(l => ({ ...l, porcentaje: Math.round((l.cantidad / max) * 100) }));
  });

  // ---- Inventario ----
  productosBajoStock = computed(() => this.data.productos().filter(p => p.existencias <= p.minimo));
  clientesTotal = computed(() => this.data.clientes().length);

  formatPrecio(valor: number): string {
    return '$' + Math.round(valor).toLocaleString('es-CO');
  }

  exportarVentasCsv() {
    const filas = [['Cliente', 'Detalle', 'Fecha', 'Total']];
    this.data.ventas().forEach(v => filas.push([v.cliente, v.detalle, v.fecha, String(v.total)]));
    const contenido = filas.map(f => f.map(campo => `"${campo.replace(/"/g, '""')}"`).join(',')).join('\n');
    const blob = new Blob([contenido], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const enlace = document.createElement('a');
    enlace.href = url;
    enlace.download = 'ventas-emilker.csv';
    enlace.click();
    URL.revokeObjectURL(url);
  }
}
