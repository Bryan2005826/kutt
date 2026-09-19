import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

export type Rol = 'CLIENTE' | 'ADMIN_NEGOCIO';

export interface AuthUser {
  nombre: string;
  correo: string;
  rol: Rol;
}

interface AuthResponse {
  token: string;
  nombre: string;
  correo: string;
  rol: Rol;
}

export interface RegistroClienteRequest {
  nombre: string;
  correo: string;
  password: string;
  rol: 'CLIENTE';
  apellidos?: string;
  telefono?: string;
  departamento?: string;
  ciudad?: string;
  genero?: string;
  fechaNacimiento?: string;
  direccion?: string;
  permiteUbicacion?: boolean;
}

export interface RegistroNegocioRequest {
  nombre: string;
  correo: string;
  password: string;
  rol: 'ADMIN_NEGOCIO';
  nombreNegocio?: string;
  rubro?: string;
  telefonoFijoNegocio?: string;
  departamentoNegocio?: string;
  direccionNegocio?: string;
  logoUrl?: string;
  portadaUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = 'http://localhost:8080/api/auth';

  usuario = signal<AuthUser | null>(this.leerUsuarioGuardado());
  token = signal<string | null>(localStorage.getItem('kutt_token'));

  constructor(private http: HttpClient, private router: Router) {}

  private leerUsuarioGuardado(): AuthUser | null {
    const raw = localStorage.getItem('kutt_usuario');
    return raw ? JSON.parse(raw) : null;
  }

  private guardarSesion(res: AuthResponse) {
    localStorage.setItem('kutt_token', res.token);
    localStorage.setItem('kutt_usuario', JSON.stringify({ nombre: res.nombre, correo: res.correo, rol: res.rol }));
    this.token.set(res.token);
    this.usuario.set({ nombre: res.nombre, correo: res.correo, rol: res.rol });
  }

  registro(datos: RegistroClienteRequest | RegistroNegocioRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/registro`, datos)
      .pipe(tap(res => this.guardarSesion(res)));
  }

  login(correo: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/login`, { correo, password })
      .pipe(tap(res => this.guardarSesion(res)));
  }

  logout() {
    localStorage.removeItem('kutt_token');
    localStorage.removeItem('kutt_usuario');
    this.token.set(null);
    this.usuario.set(null);
    this.router.navigate(['/']);
  }

  esAdminNegocio(): boolean {
    return this.usuario()?.rol === 'ADMIN_NEGOCIO';
  }
}
