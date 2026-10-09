import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { initializeApp } from 'firebase/app';
import { getAuth, GoogleAuthProvider, FacebookAuthProvider, signInWithPopup } from 'firebase/auth';
import { environment } from '../../environments/environment';

export type Rol = 'CLIENTE' | 'ADMIN_NEGOCIO' | 'SUPER_ADMIN';

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
  recaptchaToken?: string;
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
  recaptchaToken?: string;
  nombreNegocio?: string;
  rubro?: string;
  telefonoFijoNegocio?: string;
  departamentoNegocio?: string;
  direccionNegocio?: string;
  logoUrl?: string;
  portadaUrl?: string;
  // Coordenadas ya geocodificadas a partir de la dirección real del negocio,
  // para que el pin en el mapa de Descubrir quede en el sitio correcto.
  latitudNegocio?: number;
  longitudNegocio?: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = environment.apiUrl + '/auth';

  // Firebase se inicializa una sola vez, apenas arranca la app
  private firebaseApp = initializeApp(environment.firebase);
  private firebaseAuth = getAuth(this.firebaseApp);

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

  // NUEVO: ahora recibe también el recaptchaToken y lo manda al backend
  login(correo: string, password: string, recaptchaToken?: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/login`, { correo, password, recaptchaToken })
      .pipe(tap(res => this.guardarSesion(res)));
  }

  // Login/registro con Google. rol solo se usa si es la primera vez que esa
  // persona entra (para saber si es Cliente o Admin de negocio); si la cuenta
  // ya existe, el backend ignora el rol y respeta el que ya tenía.
  async loginConGoogle(rol: 'CLIENTE' | 'ADMIN_NEGOCIO'): Promise<AuthResponse> {
    const proveedor = new GoogleAuthProvider();
    const resultado = await signInWithPopup(this.firebaseAuth, proveedor);
    const idToken = await resultado.user.getIdToken();
    return this.loginConFirebase(idToken, rol);
  }

  async loginConFacebook(rol: 'CLIENTE' | 'ADMIN_NEGOCIO'): Promise<AuthResponse> {
    const proveedor = new FacebookAuthProvider();
    const resultado = await signInWithPopup(this.firebaseAuth, proveedor);
    const idToken = await resultado.user.getIdToken();
    return this.loginConFirebase(idToken, rol);
  }

  // El backend verifica ese idToken directamente con Firebase (no confiamos
  // en nada de lo que mande el navegador sin verificar), y de ahi genera
  // nuestro propio token JWT, igual que en el login normal.
  private loginConFirebase(idToken: string, rol: 'CLIENTE' | 'ADMIN_NEGOCIO'): Promise<AuthResponse> {
    return new Promise((resolve, reject) => {
      this.http.post<AuthResponse>(`${this.api}/firebase`, { idToken, rol }).subscribe({
        next: res => { this.guardarSesion(res); resolve(res); },
        error: err => reject(err)
      });
    });
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

  esSuperAdmin(): boolean {
    return this.usuario()?.rol === 'SUPER_ADMIN';
  }
}