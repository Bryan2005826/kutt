import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DataService } from '../../services/data.service';
import { AuthService } from '../../services/auth.service';
import { LoginModalComponent } from '../../components/login-modal/login-modal.component';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, LoginModalComponent],
  templateUrl: './booking.component.html',
  styleUrl: './booking.component.css'
})
export class BookingComponent {
  negocioId: number;
  paso = signal(1);
  confirmando = signal(false);
  mostrarLoginModal = signal(false);

  barberoElegido = signal<number | null>(null);
  adicionalesElegidos = signal<number[]>([]);
  fecha = '';
  hora = '';
  notas = '';
  telefono = '';

  constructor(private route: ActivatedRoute, public data: DataService, public auth: AuthService, private router: Router) {
    this.negocioId = Number(this.route.snapshot.paramMap.get('id'));

    // Multi-tenant: el equipo y los adicionales que se muestran aquí son SOLO
    // los de este negocio, no los de toda la plataforma.
    this.data.cargarBarberos(this.negocioId);
    this.data.cargarAdicionales(this.negocioId);

    const clienteExistente = this.data.clientes().find(c => c.correo === this.auth.usuario()?.correo);
    if (clienteExistente && clienteExistente.telefono && clienteExistente.telefono !== 'No registrado') {
      this.telefono = clienteExistente.telefono;
    }
  }

  negocio = computed(() => this.data.negocios().find(n => n.id === this.negocioId));

  totalAdicionales = computed(() =>
    this.data.adicionales()
      .filter(a => this.adicionalesElegidos().includes(a.id))
      .reduce((sum, a) => sum + a.precio, 0)
  );

  totalEstimado = computed(() => (this.negocio()?.servicioPrecio ?? 0) + this.totalAdicionales());

  nombresAdicionalesElegidos = computed(() =>
    this.data.adicionales()
      .filter(a => this.adicionalesElegidos().includes(a.id))
      .map(a => a.nombre)
      .join(', ')
  );

  nombreBarberoElegido = computed(() => {
    const b = this.data.barberos().find(x => x.id === this.barberoElegido());
    return b ? b.nombre : '';
  });

  elegirBarbero(id: number) {
    this.barberoElegido.set(id);
    this.paso.set(2);
  }

  toggleAdicional(id: number) {
    this.adicionalesElegidos.update(list =>
      list.includes(id) ? list.filter(x => x !== id) : [...list, id]
    );
  }

  irAFechaYHora() { this.paso.set(3); }

  irAConfirmar() {
    if (this.fecha && this.hora && this.telefono) this.paso.set(4);
  }

  volver() {
    this.paso.update(p => Math.max(1, p - 1));
  }

  confirmar() {
    // El paso final (agendar de verdad) sí necesita sesión: si aún no ha
    // iniciado sesión, mostramos el modal aquí mismo, sin perder los pasos
    // que ya llenó (barbero, servicio, fecha, hora, etc.).
    if (!this.auth.usuario()) {
      this.mostrarLoginModal.set(true);
      return;
    }
    this.confirmarDeVerdad();
  }

  loginExitoso() {
    this.mostrarLoginModal.set(false);
    this.confirmarDeVerdad();
  }

  cerrarLoginModal() {
    this.mostrarLoginModal.set(false);
  }

  private confirmarDeVerdad() {
    const usuario = this.auth.usuario();
    const nombreCliente = usuario ? usuario.nombre : 'Cliente';
    this.confirmando.set(true);
    this.data.agendarCita(
      this.negocioId, nombreCliente, this.telefono, this.barberoElegido(), this.adicionalesElegidos(), this.fecha, this.hora, this.notas,
      creada => this.router.navigate(['/pagar/cita', creada.id])
    );
  }

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
