import { Component, computed, effect, signal } from '@angular/core';
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

  private negocioIdCargado: number | null = null;

  constructor(private route: ActivatedRoute, public data: DataService) {
    this.tipo = this.route.snapshot.paramMap.get('tipo') === 'venta' ? 'venta' : 'cita';
    this.id = Number(this.route.snapshot.paramMap.get('id'));

    // Multi-tenant: en cuanto sabemos a que negocio pertenece esta cita/venta,
    // cargamos SUS metodos de pago y equipo (para el link de WhatsApp del barbero)
    effect(() => {
      const negocioId = this.item()?.negocioId;
      if (negocioId != null && negocioId !== this.negocioIdCargado) {
        this.negocioIdCargado = negocioId;
        this.data.cargarMetodosPago(negocioId);
        this.data.cargarBarberos(negocioId);
      }
    });
  }

  item = computed<Item | undefined>(() => {
    if (this.tipo === 'cita') {
      return this.data.citas().find(c => c.id === this.id);
    }
    return this.data.ventas().find(v => v.id === this.id);
  });

  negocio = computed(() => {
    const negocioId = this.item()?.negocioId;
    return negocioId != null ? this.data.negocios().find(n => n.id === negocioId) : undefined;
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

  // Genera un QR real (imagen) a partir de los datos del pago, usando un
  // servicio publico y gratuito de generacion de codigos QR. El QR guarda un
  // texto de referencia del pago/ticket, no procesa dinero real todavia.
  qrImageUrl(r: any): string {
    const referencia = this.tipo === 'cita'
      ? `KUTT-CITA-${r.id}-${r.total}-${r.metodoPago ?? 'EFECTIVO'}`
      : `KUTT-VENTA-${r.id}-${r.total}-${r.metodoPago ?? 'EFECTIVO'}`;
    const datos = encodeURIComponent(referencia);
    return `https://api.qrserver.com/v1/create-qr-code/?size=220x220&margin=8&data=${datos}`;
  }

  // NUEVO: arma el mensaje con los datos de la cita/venta y devuelve el link
  // de wa.me para abrir WhatsApp ya con el numero y el texto listos para enviar.
  private mensajeWhatsApp(r: any): string {
    if (this.tipo === 'cita') {
      const c = r as Cita;
      return `Nueva cita agendada en Kutt:\n`
        + `Cliente: ${c.cliente ?? ''}\n`
        + `Teléfono: ${c.telefonoCliente ?? ''}\n`
        + `Servicio: ${c.servicio ?? ''}${c.adicionales ? ' + ' + c.adicionales : ''}\n`
        + `Barbero: ${c.barbero ?? 'Sin preferencia'}\n`
        + `Fecha: ${c.fecha ?? ''} ${c.hora ?? ''}\n`
        + `Total a pagar el día de la cita: ${this.formatPrecio(c.total)}\n`
        + `Referencia QR: KUTT-CITA-${c.id}`;
    }
    const v = r as Venta;
    return `Nueva compra de productos en Kutt:\n`
      + `Cliente: ${v.cliente ?? ''}\n`
      + `Detalle: ${v.detalle ?? ''}\n`
      + `Total: ${this.formatPrecio(v.total)}\n`
      + `Referencia QR: KUTT-VENTA-${v.id}`;
  }

  private linkWhatsApp(numero: string | undefined | null, r: any): string {
    if (!numero) return '';
    const numeroLimpio = numero.replace(/\D/g, ''); // deja solo dígitos
    if (!numeroLimpio) return '';
    return `https://wa.me/${numeroLimpio}?text=${encodeURIComponent(this.mensajeWhatsApp(r))}`;
  }

  // WhatsApp configurado por el admin del negocio (Ajustes -> Servicio base)
  linkWhatsAppAdmin(r: any): string {
    return this.linkWhatsApp(this.negocio()?.whatsapp, r);
  }

  // WhatsApp del barbero elegido para esta cita en particular (si tiene uno registrado)
  linkWhatsAppBarbero(r: any): string {
    if (this.tipo !== 'cita') return '';
    const nombreBarbero = (r as Cita).barbero;
    const barbero = this.data.barberos().find(b => b.nombre === nombreBarbero);
    return this.linkWhatsApp(barbero?.whatsapp, r);
  }

  hayWhatsAppAdmin(): boolean {
    return !!this.negocio()?.whatsapp;
  }

  hayWhatsAppBarbero(r: any): boolean {
    if (this.tipo !== 'cita') return false;
    const nombreBarbero = (r as Cita).barbero;
    return !!this.data.barberos().find(b => b.nombre === nombreBarbero)?.whatsapp;
  }
}
