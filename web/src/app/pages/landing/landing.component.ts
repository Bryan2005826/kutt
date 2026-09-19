import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

interface Feature {
  icon: string;
  name: string;
  detail: string;
}

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent {
  // lo que va rotando en el carrusel de la home: mitad funciones, mitad beneficios
  carrusel: Feature[] = [
    { icon: 'calendar', name: 'Agenda inteligente', detail: 'Elige servicio y adicionales, sin cruces de horario' },
    { icon: 'user', name: 'Elige tu barbero', detail: 'Mira su foto y agenda con quien prefieras' },
    { icon: 'card', name: 'Pago flexible', detail: 'QR digital al instante o efectivo con ticket' },
    { icon: 'bell', name: 'Recordatorios', detail: 'El cliente no olvida su cita' },
    { icon: 'box', name: 'Compra de productos', detail: 'Gel, cera y más, sin necesidad de cita' },
    { icon: 'chart', name: 'Reportes claros', detail: 'El dueño ve qué se vende y cuándo se llena la agenda' },
    { icon: 'grid', name: 'Inventario controlado', detail: 'Alertas antes de que se agote un producto' },
    { icon: 'check', name: 'Acceso seguro', detail: 'Cada quien entra solo a lo que le corresponde' },
  ];

  appointments = [
    { time: '10:00 AM', client: 'Juan David', service: 'Corte de cabello', status: 'Confirmada' },
    { time: '11:30 AM', client: 'Carlos Torres', service: 'Barba + Corte', status: 'Confirmada' },
    { time: '2:00 PM', client: 'Andrés Felipe', service: 'Corte + Diseño', status: 'Pendiente' }
  ];

  @ViewChild('carouselTrack') carouselTrack?: ElementRef<HTMLDivElement>;

  moverCarrusel(direccion: 1 | -1) {
    const el = this.carouselTrack?.nativeElement;
    if (!el) return;
    const tarjeta = el.querySelector('.carousel-card') as HTMLElement | null;
    const ancho = tarjeta ? tarjeta.offsetWidth + 16 : 280;
    el.scrollBy({ left: direccion * ancho, behavior: 'smooth' });
  }
}
