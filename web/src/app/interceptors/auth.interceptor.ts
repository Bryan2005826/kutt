import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token();

  const headers: Record<string, string> = {};
  if (token) headers['Authorization'] = `Bearer ${token}`;

  // Mientras el backend pasa por un túnel gratis de ngrok, ngrok intercepta
  // toda petición que no traiga este header con una página HTML de
  // advertencia, y el frontend (que espera JSON) falla en silencio.
  headers['ngrok-skip-browser-warning'] = 'true';

  req = req.clone({ setHeaders: headers });

  return next(req);
};