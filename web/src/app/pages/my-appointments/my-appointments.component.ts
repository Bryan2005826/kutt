import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DataService } from '../../services/data.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-my-appointments',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './my-appointments.component.html',
  styleUrl: './my-appointments.component.css'
})
export class MyAppointmentsComponent {
  citaEnEdicion = signal<number | null>(null);
  nuevaFecha = '';
  nuevaHora = '';

  citaCalificando = signal<number | null>(null);
  calificacionSeleccionada = signal(5);
  comentarioCalificacion = '';
  calificacionEnviada = signal<number | null>(null);

  constructor(public data: DataService, public auth: AuthService) {}

  misCitas = computed(() => {
    const usuario = this.auth.usuario();
    if (!usuario) return [];
    return this.data.citas().filter(c => c.cliente === usuario.nombre);
  });

  cancelar(id: number) {
    this.data.cancelarCita(id);
  }

  abrirReprogramar(id: number) {
    this.citaEnEdicion.set(id);
    this.nuevaFecha = '';
    this.nuevaHora = '';
  }

  cerrarReprogramar() {
    this.citaEnEdicion.set(null);
  }

  confirmarReprogramar() {
    const id = this.citaEnEdicion();
    if (id === null || !this.nuevaFecha || !this.nuevaHora) return;
    this.data.reprogramarCita(id, this.nuevaFecha, this.nuevaHora);
    this.citaEnEdicion.set(null);
  }

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }

  abrirCalificar(id: number) {
    this.citaCalificando.set(id);
    this.calificacionSeleccionada.set(5);
    this.comentarioCalificacion = '';
  }

  cerrarCalificar() {
    this.citaCalificando.set(null);
  }

  enviarCalificacion() {
    const id = this.citaCalificando();
    const usuario = this.auth.usuario();
    if (id === null || !usuario) return;
    // El negocio se toma como el negocio principal (id 1) hasta que las citas
    // queden asociadas a un negocio especifico en la base de datos.
    this.data.calificarNegocio(1, usuario.nombre, usuario.correo, this.calificacionSeleccionada(), this.comentarioCalificacion, () => {
      this.calificacionEnviada.set(id);
      this.citaCalificando.set(null);
    });
  }
}
