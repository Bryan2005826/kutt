import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface Cita {
  id: number;
  negocioId?: number;
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
  negocioId?: number;
  nombre: string;
  precio: number;
  existencias: number;
  minimo: number;
  fotoUrl?: string;
}

export interface Cliente {
  id: number;
  nombre: string;
  apellidos?: string;
  correo: string;
  telefono: string;
  departamento?: string;
  ciudad?: string;
  genero?: string;
  fechaNacimiento?: string;
  direccion?: string;
  permiteUbicacion?: boolean;
}

export interface Venta {
  id: number;
  negocioId?: number;
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
  negocioId?: number;
  nombre: string;
  precio: number;
}

export interface Barbero {
  id: number;
  negocioId?: number;
  nombre: string;
  fotoUrl: string;
  whatsapp?: string;
  activo: boolean;
}

export interface MetodoPago {
  id: number;
  negocioId?: number;
  nombre: string;
  tipo: 'DIGITAL' | 'EFECTIVO';
  cuenta: string;
}

export interface ItemCompra {
  productoId: number;
  cantidad: number;
}

export interface Negocio {
  id: number;
  nombre: string;
  rubro: 'BARBERIA' | 'UNAS' | 'ESTETICA' | 'SPA' | 'OTRO';
  telefonoFijo: string;
  departamento: string;
  direccion: string;
  logoUrl?: string;
  portadaUrl?: string;
  adminCorreo: string;
  verificado: boolean;
  latitud?: number;
  longitud?: number;
  // NUEVO: cada negocio tiene su propio servicio base, precio y WhatsApp
  // (antes esto era una config global compartida por todos los negocios)
  servicioNombre?: string;
  servicioPrecio?: number;
  whatsapp?: string;
  notificacionDestino?: string;
}

export interface Resena {
  id: number;
  negocioId: number;
  clienteNombre: string;
  clienteCorreo: string;
  calificacion: number;
  comentario: string;
  fecha: string;
}

export interface Favorito {
  id: number;
  clienteCorreo: string;
  negocioId: number;
}

export interface ResumenPlataforma {
  totalNegocios: number;
  negociosVerificados: number;
  negociosPorRubro: Record<string, number>;
  totalClientes: number;
  totalCitas: number;
  negocios: Negocio[];
}

@Injectable({ providedIn: 'root' })
export class DataService {
  private api = environment.apiUrl;

  clientes = signal<Cliente[]>([]);
  productos = signal<ProductoInventario[]>([]);
  citas = signal<Cita[]>([]);
  ventas = signal<Venta[]>([]);
  adicionales = signal<Adicional[]>([]);
  metodosPago = signal<MetodoPago[]>([]);
  barberos = signal<Barbero[]>([]);
  negocios = signal<Negocio[]>([]);
  resenas = signal<Resena[]>([]);
  favoritos = signal<Favorito[]>([]);

  fechasDisponibles = ['10 mayo', '11 mayo', '12 mayo', '13 mayo'];
  horarios = ['9:00 AM', '10:00 AM', '11:00 AM', '12:00 PM', '2:00 PM', '3:00 PM', '4:00 PM', '5:00 PM'];

  /** true mientras no se ha podido contactar el backend (ej. no est\u00e1 corriendo local) */
  sinConexion = signal(false);

  constructor(private http: HttpClient) {
    this.cargarTodo();
  }

  // Multi-tenant: lo que se carga aqui al arrancar es "de toda la plataforma"
  // (el directorio de negocios, y las citas/ventas que cada quien ve segun su
  // rol). Lo que es propio de UN negocio (barberos, productos, adicionales,
  // metodos de pago) se carga aparte, ya con el negocioId concreto — ver
  // cargarBarberos/cargarProductos/cargarAdicionales/cargarMetodosPago mas abajo.
  private cargarTodo() {
    this.http.get<Cliente[]>(`${this.api}/clientes`).subscribe({
      next: list => { this.clientes.set(list); this.sinConexion.set(false); },
      error: () => this.sinConexion.set(true)
    });
    this.http.get<Cita[]>(`${this.api}/citas`).subscribe({
      next: list => this.citas.set(list), error: () => this.sinConexion.set(true)
    });
    this.http.get<Venta[]>(`${this.api}/ventas`).subscribe({
      next: list => this.ventas.set(list), error: () => this.sinConexion.set(true)
    });
    this.http.get<Negocio[]>(`${this.api}/negocios`).subscribe({
      next: list => this.negocios.set(list), error: () => this.sinConexion.set(true)
    });
    this.http.get<Resena[]>(`${this.api}/resenas`).subscribe({
      next: list => this.resenas.set(list), error: () => this.sinConexion.set(true)
    });
  }

  // ---- Carga de datos propios de UN negocio (multi-tenant) ----
  // Con negocioId: lo usa el cliente que esta viendo el perfil de ese negocio
  // (publico, sin sesion). Sin negocioId: lo usa el panel del admin logueado,
  // y el backend averigua solo cual es su negocio a partir del token.
  cargarBarberos(negocioId?: number) {
    const params: Record<string, number> = negocioId != null ? { negocioId } : {};
    this.http.get<Barbero[]>(`${this.api}/barberos`, { params }).subscribe({
      next: list => this.barberos.set(list), error: () => this.sinConexion.set(true)
    });
  }

  cargarProductos(negocioId?: number) {
    const params: Record<string, number> = negocioId != null ? { negocioId } : {};
    this.http.get<ProductoInventario[]>(`${this.api}/productos`, { params }).subscribe({
      next: list => this.productos.set(list), error: () => this.sinConexion.set(true)
    });
  }

  cargarAdicionales(negocioId?: number) {
    const params: Record<string, number> = negocioId != null ? { negocioId } : {};
    this.http.get<Adicional[]>(`${this.api}/adicionales`, { params }).subscribe({
      next: list => this.adicionales.set(list), error: () => this.sinConexion.set(true)
    });
  }

  cargarMetodosPago(negocioId?: number) {
    const params: Record<string, number> = negocioId != null ? { negocioId } : {};
    this.http.get<MetodoPago[]>(`${this.api}/metodos-pago`, { params }).subscribe({
      next: list => this.metodosPago.set(list), error: () => this.sinConexion.set(true)
    });
  }

  cargarCitas() {
    this.http.get<Cita[]>(`${this.api}/citas`).subscribe({
      next: list => this.citas.set(list), error: () => this.sinConexion.set(true)
    });
  }

  cargarVentas() {
    this.http.get<Venta[]>(`${this.api}/ventas`).subscribe({
      next: list => this.ventas.set(list), error: () => this.sinConexion.set(true)
    });
  }

  // El admin acaba de iniciar sesión: refresca todo lo que ya se había cargado
  // sin sesión, ahora sí filtrado a su propio negocio (agenda, ventas, equipo,
  // inventario, adicionales y métodos de pago).
  cargarPanelAdmin() {
    this.cargarBarberos();
    this.cargarProductos();
    this.cargarAdicionales();
    this.cargarMetodosPago();
    this.cargarCitas();
    this.cargarVentas();
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

  editarCliente(id: number, datos: Partial<Cliente>) {
    this.http.put<Cliente>(`${this.api}/clientes/${id}`, datos).subscribe({
      next: actualizado => this.clientes.update(list => list.map(c => c.id === id ? actualizado : c)),
      error: () => this.sinConexion.set(true)
    });
  }

  // ---- CU-04: agendar cita (barbero + servicio base + adicionales), siempre para un negocio concreto ----
  agendarCita(negocioId: number, cliente: string, telefonoCliente: string, barberoId: number | null, adicionalIds: number[], fecha: string, hora: string, notas: string, alTerminar?: (cita: Cita) => void) {
    this.http.post<Cita>(`${this.api}/citas`, { negocioId, cliente, telefonoCliente, barberoId, adicionalIds, fecha, hora, notas }).subscribe({
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

  // ---- CU-07: comprar productos de forma independiente, siempre para un negocio concreto ----
  comprarProductos(negocioId: number, cliente: string, items: ItemCompra[], alTerminar?: (venta: Venta) => void) {
    this.http.post<Venta>(`${this.api}/ventas/compra`, { negocioId, cliente, items }).subscribe({
      next: creada => {
        this.ventas.update(list => [...list, creada]);
        // refresca el inventario de ESE negocio porque el backend ya descont\u00f3 existencias
        this.cargarProductos(negocioId);
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
  agregarProducto(nombre: string, precio: number, existencias: number, minimo: number, fotoUrl: string) {
    this.http.post<ProductoInventario>(`${this.api}/productos`, { nombre, precio, existencias, minimo, fotoUrl }).subscribe({
      next: creado => this.productos.update(list => [...list, creado]),
      error: () => this.sinConexion.set(true)
    });
  }

  // El admin edita un producto ya creado: precio, foto, o la cantidad (por
  // ejemplo cuando le llega mercancia nueva y hay que actualizar el stock).
  editarProducto(id: number, datos: Partial<ProductoInventario>) {
    this.http.put<ProductoInventario>(`${this.api}/productos/${id}`, datos).subscribe({
      next: actualizado => this.productos.update(list => list.map(p => p.id === id ? actualizado : p)),
      error: () => this.sinConexion.set(true)
    });
  }

  // El admin borra un producto que ya no maneja (descontinuado, se acabó el proveedor, etc.)
  eliminarProducto(id: number, alTerminar?: () => void, alFallar?: () => void) {
    this.http.delete(`${this.api}/productos/${id}`).subscribe({
      next: () => {
        this.productos.update(list => list.filter(p => p.id !== id));
        if (alTerminar) alTerminar();
      },
      error: () => {
        this.sinConexion.set(true);
        if (alFallar) alFallar();
      }
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
  agregarBarbero(nombre: string, fotoUrl: string, whatsapp: string = '') {
    this.http.post<Barbero>(`${this.api}/barberos`, { nombre, fotoUrl, whatsapp, activo: true }).subscribe({
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

  // ---- Calificar el servicio despues de una cita ----
  calificarNegocio(negocioId: number, clienteNombre: string, clienteCorreo: string, calificacion: number, comentario: string, alTerminar?: () => void) {
    const fecha = new Date().toLocaleDateString('es-CO');
    this.http.post<Resena>(`${this.api}/resenas`, { negocioId, clienteNombre, clienteCorreo, calificacion, comentario, fecha }).subscribe({
      next: creada => {
        this.resenas.update(list => [...list, creada]);
        if (alTerminar) alTerminar();
      },
      error: () => this.sinConexion.set(true)
    });
  }

  resenasDe(negocioId: number) {
    return this.resenas().filter(r => r.negocioId === negocioId);
  }

  promedioCalificacion(negocioId: number): number {
    const lista = this.resenasDe(negocioId);
    if (lista.length === 0) return 0;
    return lista.reduce((sum, r) => sum + r.calificacion, 0) / lista.length;
  }

  // ---- Favoritos ----
  cargarFavoritos(clienteCorreo: string) {
    this.http.get<Favorito[]>(`${this.api}/favoritos/${clienteCorreo}`).subscribe({
      next: list => this.favoritos.set(list),
      error: () => this.sinConexion.set(true)
    });
  }

  esFavorito(negocioId: number): boolean {
    return this.favoritos().some(f => f.negocioId === negocioId);
  }

  alternarFavorito(clienteCorreo: string, negocioId: number) {
    if (this.esFavorito(negocioId)) {
      this.http.delete(`${this.api}/favoritos`, { params: { clienteCorreo, negocioId } }).subscribe({
        next: () => this.favoritos.update(list => list.filter(f => f.negocioId !== negocioId)),
        error: () => this.sinConexion.set(true)
      });
    } else {
      this.http.post<Favorito>(`${this.api}/favoritos`, { clienteCorreo, negocioId }).subscribe({
        next: creado => this.favoritos.update(list => [...list, creado]),
        error: () => this.sinConexion.set(true)
      });
    }
  }

  // ---- Panel de Super Admin de la plataforma ----
  cargarResumenPlataforma(alTerminar: (resumen: ResumenPlataforma) => void) {
    this.http.get<ResumenPlataforma>(`${this.api}/plataforma/resumen`).subscribe({
      next: r => alTerminar(r),
      error: () => this.sinConexion.set(true)
    });
  }

  // Sube una foto elegida de la galeria/computador (en vez de tener que pegar
  // una URL a mano) y devuelve, por el callback, la direccion donde quedo guardada.
  subirFoto(archivo: File, alTerminar: (url: string) => void, alFallar?: () => void) {
    const formData = new FormData();
    formData.append('archivo', archivo);
    this.http.post<{ url: string }>(`${this.api}/archivos/subir`, formData).subscribe({
      next: res => alTerminar(res.url),
      error: () => {
        this.sinConexion.set(true);
        if (alFallar) alFallar();
      }
    });
  }

  // ---- Geocodificación: convierte "Carrera 5#8-40, Paz de Ariporo" en lat/lon reales ----
  // Usa Nominatim (OpenStreetMap), el mismo proveedor gratuito que ya usamos para el
  // mapa de Descubrir. Se llama al crear o editar la dirección de un negocio, para que
  // el pin del mapa quede en el sitio real y no en una coordenada inventada.
  geocodificarDireccion(direccion: string, departamento: string) {
    const texto = [direccion, departamento, 'Colombia'].filter(Boolean).join(', ');
    const url = `https://nominatim.openstreetmap.org/search?format=json&limit=1&q=${encodeURIComponent(texto)}`;
    return this.http.get<{ lat: string; lon: string }[]>(url);
  }

  // ---- El admin ve y edita los datos de su propio negocio (logo, portada, etc.) ----
  cargarMiNegocio(alTerminar: (negocio: Negocio) => void) {
    this.http.get<Negocio>(`${this.api}/negocios/mi-negocio`).subscribe({
      next: n => alTerminar(n),
      error: () => this.sinConexion.set(true)
    });
  }

  actualizarMiNegocio(datos: Partial<Negocio>, alTerminar?: (negocio: Negocio) => void) {
    this.http.put<Negocio>(`${this.api}/negocios/mi-negocio`, datos).subscribe({
      next: actualizado => {
        this.negocios.update(list => list.map(n => n.id === actualizado.id ? actualizado : n));
        if (alTerminar) alTerminar(actualizado);
      },
      error: () => this.sinConexion.set(true)
    });
  }
}
