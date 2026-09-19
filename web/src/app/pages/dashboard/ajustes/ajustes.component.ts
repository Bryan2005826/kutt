import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../../services/data.service';

@Component({
  selector: 'app-dashboard-ajustes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ajustes.component.html',
  styleUrl: './ajustes.component.css'
})
export class AjustesComponent {
  servicioNombre = '';
  servicioPrecio = 0;
  notificacionDestino = '';

  nuevoAdicionalNombre = '';
  nuevoAdicionalPrecio = 0;

  nuevoMetodoNombre = '';
  nuevoMetodoTipo: 'DIGITAL' | 'EFECTIVO' = 'DIGITAL';
  nuevoMetodoCuenta = '';

  nuevoBarberoNombre = '';
  nuevoBarberoFoto = '';

  guardadoServicio = signal(false);

  constructor(public data: DataService) {
    const config = this.data.configuracion();
    this.servicioNombre = config.servicioNombre;
    this.servicioPrecio = config.servicioPrecio;
    this.notificacionDestino = config.notificacionDestino;
  }

  guardarServicio() {
    this.data.actualizarConfiguracion(this.servicioNombre, this.servicioPrecio, this.notificacionDestino);
    this.guardadoServicio.set(true);
    setTimeout(() => this.guardadoServicio.set(false), 2000);
  }

  agregarAdicional() {
    if (!this.nuevoAdicionalNombre || this.nuevoAdicionalPrecio <= 0) return;
    this.data.agregarAdicional(this.nuevoAdicionalNombre, this.nuevoAdicionalPrecio);
    this.nuevoAdicionalNombre = '';
    this.nuevoAdicionalPrecio = 0;
  }

  eliminarAdicional(id: number) {
    this.data.eliminarAdicional(id);
  }

  agregarMetodo() {
    if (!this.nuevoMetodoNombre || !this.nuevoMetodoCuenta) return;
    this.data.agregarMetodoPago(this.nuevoMetodoNombre, this.nuevoMetodoTipo, this.nuevoMetodoCuenta);
    this.nuevoMetodoNombre = '';
    this.nuevoMetodoCuenta = '';
    this.nuevoMetodoTipo = 'DIGITAL';
  }

  eliminarMetodo(id: number) {
    this.data.eliminarMetodoPago(id);
  }

  agregarBarbero() {
    if (!this.nuevoBarberoNombre || !this.nuevoBarberoFoto) return;
    this.data.agregarBarbero(this.nuevoBarberoNombre, this.nuevoBarberoFoto);
    this.nuevoBarberoNombre = '';
    this.nuevoBarberoFoto = '';
  }

  eliminarBarbero(id: number) {
    this.data.eliminarBarbero(id);
  }

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
