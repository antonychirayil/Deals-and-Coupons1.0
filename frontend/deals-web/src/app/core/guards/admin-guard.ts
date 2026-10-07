import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../services/auth-service';

// Only admins may open admin pages. (The gateway ALSO checks this - see Phase 5.)
export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
  }
  if (authService.isAdmin()) {
    return true;
  }
  return router.createUrlTree(['/']); // logged in, but not an admin
};
