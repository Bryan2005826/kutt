import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface Cita {
  id: number;
  cliente: string;
  telefonoCliente?: string;
  servicio: string;
  barbero?: string;
  adicionales?: string;
  fecha: string;
  hora: string;
  notas?: string;
  estado: 'Pendiente' | 'Confirmada' | 'Finalizada' | 'Cancelada';
  recordatorioEnviado?: boolean;
  total: number;
  estadoPago: 'Pendiente' | 'Pagado';
  metodoPago?: string;
  tipoQr?: 'DIGITAL' | 'TICKET';
}

export interface ProductoInventario {
  id: number;
  nombre: string;
  precio: number;
  existencias: number;
  minimo: number;
}

export interface Cliente {
  id: number;
  nombre: string;
  correo: string;
  telefono: string;
}

export interface Venta {
  id: number;
  cliente: string;
  detalle: string;
  total: number;
  fecha: string;
  estadoPago: 'Pendiente' | 'Pagado';
  metodoPago?: string;
  tipoQr?: 'DIGITAL' | 'TICKET';
}

export interface Adicional {
  id: number;
  nombre: string;
  precio: number;
}

export interface Barbero {
  id: number;
  nombre: string;
  fotoUrl: string;
  activo: boolean;
}

export interface MetodoPago {
  id: number;
  nombre: string;
  tipo: 'DIGITAL' | 'EFECTIVO';
  cuenta: string;
}

export interface Configuracion {
  servicioNombre: string;
  servicioPrecio: number;
  notificacionDestino: string;
}

export interface ItemCompra {
  productoId: number;
  cantidad: number;
}

@Injectable({ providedIn: 'root' })
export class DataService {
  private api = 'http://localhost:8080/api';

  clientes = signal<Cliente[]>([]);
  productos = signal<ProductoInventario[]>([]);
  citas = signal<Cita[]>([]);
  ventas = signal<Venta[]>([]);
  adicionales = signal<Adicional[]>([]);
  metodosPago = signal<MetodoPago[]>([]);
  barberos = signal<Barbero[]>([]);
  configuracion = signal<Configuracion>({ servicioNombre: 'Corte b\u00e1sico', servicioPrecio: 15000, notificacionDestino: '' });

  fechasDisponibles = ['10 mayo', '11 mayo', '12 mayo', '13 mayo'];
  horarios = ['9:00 AM', '10:00 AM', '11:00 AM', '12:00 PM', '2:00 PM', '3:00 PM', '4:00 PM', '5:00 PM'];

  /** true mientras no se ha podido contactar el backend (ej. no est\u00e1 corriendo local) */
  sinConexion = signal(false);

  constructor(private http: HttpClient) {
    this.cargarTodo();
  }

  private cargarTodo() {
    this.http.get<Cliente[]>(`${this.api}/clientes`).subscribe({
      next: list => { this.clientes.set(list); this.sinConexion.set(false); },
      error: () => this.sinConexion.set(true)
    });
    this.http.get<ProductoInventario[]>(`${this.api}/productos`).subscribe({
      next: list => this.productos.set(list), error: () => this.sinConexion.set(true)
    });
    this.http.get<Cita[]>(`${this.api}/citas`).subscribe({
      next: list => this.citas.set(list), error: () => this.sinConexion.set(true)
    });
    this.http.get<Venta[]>(`${this.api}/ventas`).subscribe({
      next: list => this.ventas.set(list), error: () => this.sinConexion.set(true)
    });
    this.http.get<Adicional[]>(`${this.api}/adicionales`).subscribe({
      next: list => this.adicionales.set(list), error: () => this.sinConexion.set(true)
    });
    this.http.get<MetodoPago[]>(`${this.api}/metodos-pago`).subscribe({
      next: list => this.metodosPago.set(list), error: () => this.sinConexion.set(true)
    });
    this.http.get<Barbero[]>(`${this.api}/barberos`).subscribe({
      next: list => this.barberos.set(list), error: () => this.sinConexion.set(true)
    });
    this.http.get<Configuracion>(`${this.api}/configuracion`).subscribe({
      next: c => this.configuracion.set(c), error: () => this.sinConexion.set(true)
    });
  }

  // Registra (o recupera) el Cliente asociado a una cuenta de tipo CLIENTE reci\u00e9n autenticada
  asegurarCliente(nombre: string, correo: string) {
    this.http.post<Cliente>(`${this.api}/clientes/asegurar`, { nombre, correo }).subscribe({
      next: c => {
        this.clientes.update(list =>
          list.some(x => x.correo === c.correo) ? list.map(x => x.correo === c.correo ? c : x) : [...list, c]
        );
      },
      error: () => this.sinConexion.set(true)
    });
  }

  editarCliente(id: number, nombre: string, telefono: string) {
    this.http.put<Cliente>(`${this.api}/clientes/${id}`, { nombre, telefono }).subscribe({
      next: actualizado => this.clientes.update(list => list.map(c => c.id === id ? actualizado : c)),
      error: () => this.sinConexion.set(true)
    });
  }

  // ---- CU-04: agendar cita (barbero + servicio base + adicionales) ----
  agendarCita(cliente: string, telefonoCliente: string, barberoId: number | null, adicionalIds: number[], fecha: string, hora: string, notas: string, alTerminar?: (cita: Cita) => void) {
    this.http.post<Cita>(`${this.api}/citas`, { cliente, telefonoCliente, barberoId, adicionalIds, fecha, hora, notas }).subscribe({
      next: creada => {
        this.citas.update(list => [...list, creada]);
        if (alTerminar) alTerminar(creada);
      },
      error: () => this.sinConexion.set(true)
    });
  }

  // ---- CU-06: aceptar / rechazar cita ----
  confirmarCita(id: number) {
    this.http.put<Cita>(`${this.api}/citas/${id}/confirmar`, {}).subscribe({
      next: actualizada => this.citas.update(list => list.map(c => c.id === id ? actualizada : c)),
      error: () => this.sinConexion.set(true)
    });
  }

  rechazarCita(id: number) {
    this.http.put<Cita>(`${this.api}/citas/${id}/rechazar`, {}).subscribe({
      next: actualizada => this.citas.update(list => list.map(c => c.id === id ? actualizada : c)),
      error: () => this.sinConexion.set(true)
    });
  }

  cancelarCita(id: number) {
    this.http.put<Cita>(`${this.api}/citas/${id}/cancelar`, {}).subscribe({
      next: actualizada => this.citas.update(list => list.map(c => c.id === id ? actualizada : c)),
      error: () => this.sinConexion.set(true)
    });
  }

  reprogramarCita(id: number, fecha: string, hora: string) {
    this.http.put<Cita>(`${this.api}/citas/${id}/reprogramar`, { fecha, hora }).subscribe({
      next: actualizada => this.citas.update(list => list.map(c => c.id === id ? actualizada : c)),
      error: () => this.sinConexion.set(true)
    });
  }

  finalizarCita(id: number) {
    this.http.put<Cita>(`${this.api}/citas/${id}/finalizar`, {}).subscribe({
      next: actualizada => this.citas.update(list => list.map(c => c.id === id ? actualizada : c)),
      error: () => this.sinConexion.set(true)
    });
  }

  // ---- CU-08: generar el pago de una cita ----
  pagarCita(id: number, metodoPagoId: number, alTerminar?: (cita: Cita) => void) {
    this.http.post<Cita>(`${this.api}/citas/${id}/pagar`, { metodoPagoId }).subscribe({
      next: actualizada => {
        this.citas.update(list => list.map(c => c.id === id ? actualizada : c));
        if (alTerminar) alTerminar(actualizada);
      },
      error: () => this.sinConexion.set(true)
    });
  }

  // ---- CU-07: comprar productos de forma independiente ----
  comprarProductos(cliente: string, items: ItemCompra[], alTerminar?: (venta: Venta) => void) {
    this.http.post<Venta>(`${this.api}/ventas/compra`, { cliente, items }).subscribe({
      next: creada => {
        this.ventas.update(list => [...list, creada]);
        // refresca inventario porque el backend ya descont\u00f3 existencias
        this.http.get<ProductoInventario[]>(`${this.api}/productos`).subscribe(list => this.productos.set(list));
        if (alTerminar) alTerminar(creada);
      },
      error: () => this.sinConexion.set(true)
    });
  }

  // ---- CU-08: generar el pago de una compra de productos ----
  pagarVenta(id: number, metodoPagoId: number, alTerminar?: (venta: Venta) => void) {
    this.http.post<Venta>(`${this.api}/ventas/${id}/pagar`, { metodoPagoId }).subscribe({
      next: actualizada => {
        this.ventas.update(list => list.map(v => v.id === id ? actualizada : v));
        if (alTerminar) alTerminar(actualizada);
      },
      error: () => this.sinConexion.set(true)
    });
  }

  registrarVenta(cliente: string, detalle: string, total: number, fecha: string) {
    this.http.post<Venta>(`${this.api}/ventas`, { cliente, detalle, total, fecha }).subscribe({
      next: creada => this.ventas.update(list => [...list, creada]),
      error: () => this.sinConexion.set(true)
    });
  }

  // ---- CU-03: inventario ----
  agregarProducto(nombre: string, precio: number, existencias: number, minimo: number) {
    this.http.post<ProductoInventario>(`${this.api}/productos`, { nombre, precio, existencias, minimo }).subscribe({
      next: creado => this.productos.update(list => [...list, creado]),
      error: () => this.sinConexion.set(true)
    });
  }

  // ---- CU-03: servicios y adicionales (Super Admin) ----
  agregarAdicional(nombre: string, precio: number) {
    this.http.post<Adicional>(`${this.api}/adicionales`, { nombre, precio }).subscribe({
      next: creado => this.adicionales.update(list => [...list, creado]),
      error: () => this.sinConexion.set(true)
    });
  }

  eliminarAdicional(id: number) {
    this.http.delete(`${this.api}/adicionales/${id}`).subscribe({
      next: () => this.adicionales.update(list => list.filter(a => a.id !== id)),
      error: () => this.sinConexion.set(true)
    });
  }

  // ---- CU-09: m\u00e9todos de pago y configuraci\u00f3n ----
  agregarMetodoPago(nombre: string, tipo: 'DIGITAL' | 'EFECTIVO', cuenta: string) {
    this.http.post<MetodoPago>(`${this.api}/metodos-pago`, { nombre, tipo, cuenta }).subscribe({
      next: creado => this.metodosPago.update(list => [...list, creado]),
      error: () => this.sinConexion.set(true)
    });
  }

  eliminarMetodoPago(id: number) {
    this.http.delete(`${this.api}/metodos-pago/${id}`).subscribe({
      next: () => this.metodosPago.update(list => list.filter(m => m.id !== id)),
      error: () => this.sinConexion.set(true)
    });
  }

  // ---- Gesti\u00f3n de barberos en servicio (Super Admin) ----
  agregarBarbero(nombre: string, fotoUrl: string) {
    this.http.post<Barbero>(`${this.api}/barberos`, { nombre, fotoUrl, activo: true }).subscribe({
      next: creado => this.barberos.update(list => [...list, creado]),
      error: () => this.sinConexion.set(true)
    });
  }

  eliminarBarbero(id: number) {
    this.http.delete(`${this.api}/barberos/${id}`).subscribe({
      next: () => this.barberos.update(list => list.filter(b => b.id !== id)),
      error: () => this.sinConexion.set(true)
    });
  }

  actualizarConfiguracion(servicioNombre: string, servicioPrecio: number, notificacionDestino: string) {
    this.http.put<Configuracion>(`${this.api}/configuracion`, { servicioNombre, servicioPrecio, notificacionDestino }).subscribe({
      next: c => this.configuracion.set(c),
      error: () => this.sinConexion.set(true)
    });
  }
}
