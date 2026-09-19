import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const clienteGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.usuario()) return true;

  router.navigate(['/login']);
  return false;
};

export const adminNegocioGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.esAdminNegocio()) return true;

  router.navigate(['/login']);
  return false;
};
