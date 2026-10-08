import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild, computed, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import * as L from 'leaflet';
import { DataService, Negocio } from '../../services/data.service';
import { AuthService } from '../../services/auth.service';
import { LoginModalComponent } from '../../components/login-modal/login-modal.component';

const NOMBRES_RUBRO: Record<string, string> = {
  BARBERIA: 'Barbería', UNAS: 'Manicura', ESTETICA: 'Centro de estética', SPA: 'Spa', OTRO: 'Otro',
};

// Coordenadas de Yopal, Casanare. Se usan como centro del mapa mientras el
// navegador no nos deje ver la ubicacion real de quien esta usando la pagina.
const CENTRO_POR_DEFECTO: [number, number] = [5.3378, -72.3959];

// El icono por defecto de Leaflet no carga bien con Angular (los "asset paths"
// se rompen al compilar). Antes apuntábamos a unpkg.com (un CDN externo), pero
// si la red del que lo usa bloquea ese dominio (redes de universidad, algunos
// antivirus/firewalls corporativos, etc.) el marcador se queda invisible sin
// ningún error visible. Por eso ahora usamos una copia local de esos mismos
// iconos, empaquetada dentro de la propia app — no depende de internet.
const iconoMarcador = L.icon({
  iconUrl: 'assets/leaflet/marker-icon.png',
  iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
  shadowUrl: 'assets/leaflet/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
});

@Component({
  selector: 'app-descubrir',
  standalone: true,
  imports: [CommonModule, RouterLink, LoginModalComponent],
  templateUrl: './descubrir.component.html',
  styleUrl: './descubrir.component.css'
})
export class DescubrirComponent implements AfterViewInit, OnDestroy {
  rubroActivo = signal<string>('TODOS');
  busqueda = signal('');
  menuCategoriasAbierto = signal(false);

  rubros = ['TODOS', 'BARBERIA', 'UNAS', 'ESTETICA', 'SPA'];

  // Modal de login: se abre cuando alguien sin sesión intenta dar like
  mostrarLoginModal = signal(false);
  private accionPendiente: (() => void) | null = null;

  // referencia al <div> donde Leaflet va a "montar" el mapa
  @ViewChild('mapaEl') mapaEl?: ElementRef<HTMLDivElement>;
  private mapa?: L.Map;
  private capaMarcadores?: L.LayerGroup;

  constructor(public data: DataService, public auth: AuthService, private router: Router) {
    const correo = this.auth.usuario()?.correo;
    if (correo) this.data.cargarFavoritos(correo);

    // cada vez que cambian los negocios filtrados (por busqueda o por rubro),
    // volvemos a pintar los marcadores del mapa
    effect(() => {
      this.pintarMarcadores(this.negociosFiltrados());
    });
  }

  ngAfterViewInit() {
    // el mapa se crea despues de que Angular ya puso el <div> en la pantalla
    if (!this.mapaEl) return;

    this.mapa = L.map(this.mapaEl.nativeElement).setView(CENTRO_POR_DEFECTO, 14);

    // capa de "mosaicos" gratis de OpenStreetMap, sin necesidad de API key
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap',
      maxZoom: 19,
    }).addTo(this.mapa);

    this.capaMarcadores = L.layerGroup().addTo(this.mapa);
    this.pintarMarcadores(this.negociosFiltrados());

    // si el usuario da permiso, centramos el mapa en su ubicacion real
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        pos => this.mapa?.setView([pos.coords.latitude, pos.coords.longitude], 14),
        () => { /* si no da permiso, nos quedamos con el centro por defecto */ }
      );
    }
  }

  ngOnDestroy() {
    this.mapa?.remove();
  }

  private pintarMarcadores(negocios: Negocio[]) {
    if (!this.capaMarcadores) return;
    this.capaMarcadores.clearLayers();

    for (const n of negocios) {
      if (n.latitud == null || n.longitud == null) continue;

      const popupHtml = `
        <div style="font-family:Inter,sans-serif;min-width:160px">
          <strong>${n.nombre}</strong><br>
          <span style="color:#888;font-size:12px">${this.nombreRubro(n.rubro)} · ${n.direccion}</span>
        </div>`;

      L.marker([n.latitud, n.longitud], { icon: iconoMarcador })
        .addTo(this.capaMarcadores)
        .bindPopup(popupHtml)
        .on('click', () => this.router.navigate(['/negocio', n.id]));
    }
  }

  negociosFiltrados = computed(() => {
    const rubro = this.rubroActivo();
    const texto = this.busqueda().toLowerCase().trim();
    return this.data.negocios().filter(n => {
      const pasaRubro = rubro === 'TODOS' || n.rubro === rubro;
      const pasaTexto = !texto || n.nombre.toLowerCase().includes(texto) || n.direccion.toLowerCase().includes(texto);
      return pasaRubro && pasaTexto;
    });
  });

  nombreRubro(r: string): string {
    return NOMBRES_RUBRO[r] ?? r;
  }

  toggleFavorito(negocio: Negocio, evento: Event) {
    evento.stopPropagation();
    evento.preventDefault();
    this.ejecutarConSesion(() => {
      const correo = this.auth.usuario()?.correo;
      if (correo) this.data.alternarFavorito(correo, negocio.id);
    });
  }

  // Si ya hay sesión, ejecuta la acción de una vez; si no, abre el modal de
  // login y guarda la acción para completarla apenas inicie sesión.
  private ejecutarConSesion(accion: () => void) {
    if (this.auth.usuario()) { accion(); return; }
    this.accionPendiente = accion;
    this.mostrarLoginModal.set(true);
  }

  loginExitoso() {
    this.mostrarLoginModal.set(false);
    const correo = this.auth.usuario()?.correo;
    if (correo) this.data.cargarFavoritos(correo);
    if (this.accionPendiente) {
      const accion = this.accionPendiente;
      this.accionPendiente = null;
      accion();
    }
  }

  cerrarLoginModal() {
    this.mostrarLoginModal.set(false);
    this.accionPendiente = null;
  }

  toggleMenuCategorias() {
    this.menuCategoriasAbierto.update(v => !v);
  }

  elegirRubro(r: string) {
    this.rubroActivo.set(r);
    this.menuCategoriasAbierto.set(false);
  }

  formatCalificacion(n: Negocio): string {
    const prom = this.data.promedioCalificacion(n.id);
    return prom > 0 ? prom.toFixed(1) : 'Nuevo';
  }

  totalResenas(n: Negocio): number {
    return this.data.resenasDe(n.id).length;
  }
}
