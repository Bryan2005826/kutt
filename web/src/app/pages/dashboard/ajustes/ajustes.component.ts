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
  whatsappAdmin = ''; // NUEVO — número donde le llega el QR de cada cita/venta

  // datos del negocio (logo, portada, nombre, etc.), se cargan al entrar a Ajustes
  negocioId: number | null = null;
  negocioNombre = '';
  negocioLogoUrl = '';
  negocioPortadaUrl = '';
  negocioDepartamento = '';
  negocioDireccion = '';
  subiendoLogo = signal(false);
  subiendoPortada = signal(false);
  guardadoNegocio = signal(false);
  ubicandoNegocio = signal(false);

  nuevoAdicionalNombre = '';
  nuevoAdicionalPrecio = 0;

  nuevoMetodoNombre = '';
  nuevoMetodoTipo: 'DIGITAL' | 'EFECTIVO' = 'DIGITAL';
  nuevoMetodoCuenta = '';

  nuevoBarberoNombre = '';
  nuevoBarberoFoto = '';
  nuevoBarberoWhatsapp = ''; // NUEVO
  subiendoFotoBarbero = signal(false);

  guardadoServicio = signal(false);

  constructor(public data: DataService) {
    // Multi-tenant: el servicio base, su precio, el WhatsApp y las
    // notificaciones ahora viven DENTRO del propio Negocio (antes eran una
    // configuración global compartida por todos los negocios). Se cargan
    // todos juntos en la misma llamada a mi-negocio.
    this.data.cargarMiNegocio(n => {
      this.negocioId = n.id;
      this.negocioNombre = n.nombre;
      this.negocioLogoUrl = n.logoUrl ?? '';
      this.negocioPortadaUrl = n.portadaUrl ?? '';
      this.negocioDepartamento = n.departamento ?? '';
      this.negocioDireccion = n.direccion ?? '';
      this.servicioNombre = n.servicioNombre ?? '';
      this.servicioPrecio = n.servicioPrecio ?? 0;
      this.notificacionDestino = n.notificacionDestino ?? '';
      this.whatsappAdmin = n.whatsapp ?? '';
    });
  }

  // el admin elige el logo desde su galeria/computador
  elegirLogo(evento: Event) {
    const archivo = (evento.target as HTMLInputElement).files?.[0];
    if (!archivo) return;
    this.subiendoLogo.set(true);
    this.data.subirFoto(
      archivo,
      url => { this.negocioLogoUrl = url; this.subiendoLogo.set(false); },
      () => this.subiendoLogo.set(false)
    );
  }

  // y lo mismo para la foto de portada del negocio
  elegirPortada(evento: Event) {
    const archivo = (evento.target as HTMLInputElement).files?.[0];
    if (!archivo) return;
    this.subiendoPortada.set(true);
    this.data.subirFoto(
      archivo,
      url => { this.negocioPortadaUrl = url; this.subiendoPortada.set(false); },
      () => this.subiendoPortada.set(false)
    );
  }

  // Ambos paneles ("Mi negocio" y "Servicio base") guardan en el MISMO
  // endpoint (PUT /negocios/mi-negocio), porque ahora todo vive en el mismo
  // Negocio. El backend ya solo actualiza los campos que vengan informados,
  // pero igual mandamos el estado completo del panel para que no queden
  // campos desactualizados entre pestañas abiertas.
  private datosCompletosDelNegocio(latitud?: number, longitud?: number) {
    return {
      nombre: this.negocioNombre,
      logoUrl: this.negocioLogoUrl,
      portadaUrl: this.negocioPortadaUrl,
      departamento: this.negocioDepartamento,
      direccion: this.negocioDireccion,
      servicioNombre: this.servicioNombre,
      servicioPrecio: this.servicioPrecio,
      whatsapp: this.whatsappAdmin,
      notificacionDestino: this.notificacionDestino,
      ...(latitud != null && longitud != null ? { latitud, longitud } : {}),
    };
  }

  guardarNegocio() {
    // Si la dirección o el departamento cambiaron, volvemos a ubicar el pin
    // del mapa antes de guardar, para que el negocio aparezca donde de
    // verdad queda (y no en una coordenada vieja o inventada).
    this.ubicandoNegocio.set(true);
    this.data.geocodificarDireccion(this.negocioDireccion, this.negocioDepartamento).subscribe({
      next: resultados => {
        const primero = resultados?.[0];
        this.ubicandoNegocio.set(false);
        this.data.actualizarMiNegocio(this.datosCompletosDelNegocio(
          primero ? Number(primero.lat) : undefined,
          primero ? Number(primero.lon) : undefined
        ));
        this.guardadoNegocio.set(true);
        setTimeout(() => this.guardadoNegocio.set(false), 2000);
      },
      error: () => {
        this.ubicandoNegocio.set(false);
        this.data.actualizarMiNegocio(this.datosCompletosDelNegocio());
        this.guardadoNegocio.set(true);
        setTimeout(() => this.guardadoNegocio.set(false), 2000);
      }
    });
  }

  guardarServicio() {
    this.data.actualizarMiNegocio(this.datosCompletosDelNegocio());
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
    this.data.agregarBarbero(this.nuevoBarberoNombre, this.nuevoBarberoFoto, this.nuevoBarberoWhatsapp);
    this.nuevoBarberoNombre = '';
    this.nuevoBarberoFoto = '';
    this.nuevoBarberoWhatsapp = '';
  }

  // se dispara cuando el admin elige una foto de su galeria/computador
  elegirFotoBarbero(evento: Event) {
    const input = evento.target as HTMLInputElement;
    const archivo = input.files?.[0];
    if (!archivo) return;

    this.subiendoFotoBarbero.set(true);
    this.data.subirFoto(
      archivo,
      url => { this.nuevoBarberoFoto = url; this.subiendoFotoBarbero.set(false); },
      () => this.subiendoFotoBarbero.set(false)
    );
  }

  eliminarBarbero(id: number) {
    this.data.eliminarBarbero(id);
  }

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
