import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
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

  constructor(public data: DataService, public auth: AuthService) {}
}
