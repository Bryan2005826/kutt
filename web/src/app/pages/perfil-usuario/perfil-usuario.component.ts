import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DataService } from '../../services/data.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-perfil-usuario',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './perfil-usuario.component.html',
  styleUrl: './perfil-usuario.component.css'
})
export class PerfilUsuarioComponent {
  guardado = signal(false);

  apellidos = '';
  telefono = '';
  departamento = '';
  ciudad = '';
  genero = '';
  fechaNacimiento = '';
  direccion = '';
  permiteUbicacion = false;

  constructor(public data: DataService, public auth: AuthService) {
    const cliente = this.clienteActual();
    if (cliente) {
      this.apellidos = cliente.apellidos ?? '';
      this.telefono = cliente.telefono ?? '';
      this.departamento = cliente.departamento ?? '';
      this.ciudad = cliente.ciudad ?? '';
      this.genero = cliente.genero ?? '';
      this.fechaNacimiento = cliente.fechaNacimiento ?? '';
      this.direccion = cliente.direccion ?? '';
      this.permiteUbicacion = cliente.permiteUbicacion ?? false;
    }
  }

  clienteActual = computed(() =>
    this.data.clientes().find(c => c.correo === this.auth.usuario()?.correo)
  );

  citasDelCliente = computed(() => {
    const nombre = this.auth.usuario()?.nombre;
    return this.data.citas().filter(c => c.cliente === nombre);
  });

  guardar() {
    const cliente = this.clienteActual();
    if (!cliente) return;
    this.data.editarCliente(cliente.id, {
      nombre: cliente.nombre,
      apellidos: this.apellidos,
      telefono: this.telefono,
      departamento: this.departamento,
      ciudad: this.ciudad,
      genero: this.genero,
      fechaNacimiento: this.fechaNacimiento,
      direccion: this.direccion,
      permiteUbicacion: this.permiteUbicacion,
    });
    this.guardado.set(true);
    setTimeout(() => this.guardado.set(false), 2000);
  }
}
