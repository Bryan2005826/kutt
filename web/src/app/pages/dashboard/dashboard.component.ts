import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet, NavigationEnd, Router } from '@angular/router';
import { DataService } from '../../services/data.service';
import { AuthService } from '../../services/auth.service';

interface NavItem { icon: string; label: string; path: string; }

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  navItems: NavItem[] = [
    { icon: 'home', label: 'Inicio', path: '/dashboard' },
    { icon: 'calendar', label: 'Agenda', path: '/dashboard/agenda' },
    { icon: 'user', label: 'Clientes', path: '/dashboard/clientes' },
    { icon: 'card', label: 'Ventas', path: '/dashboard/ventas' },
    { icon: 'box', label: 'Inventario', path: '/dashboard/inventario' },
    { icon: 'chart', label: 'Reportes', path: '/dashboard/reportes' },
    { icon: 'settings', label: 'Ajustes', path: '/dashboard/ajustes' },
  ];

  menuAbierto = signal(false);

  constructor(public data: DataService, public auth: AuthService, router: Router) {
    // Multi-tenant: apenas entra el admin al panel, recargamos todo (agenda,
    // ventas, equipo, inventario, adicionales, métodos de pago) ya filtrado a
    // SU propio negocio — lo que se cargó al arrancar la app (sin sesión) no sirve.
    this.data.cargarPanelAdmin();

    // cierra el cajon automaticamente al navegar a otra seccion
    router.events.subscribe(e => {
      if (e instanceof NavigationEnd) this.menuAbierto.set(false);
    });
  }

  toggleMenu() {
    this.menuAbierto.update(v => !v);
  }
}
