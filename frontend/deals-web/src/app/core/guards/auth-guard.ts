import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../services/auth-service';

/**
 * A guard runs BEFORE the router opens a page. Return true to allow it,
 * or a UrlTree to send the user somewhere else instead.
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }
  // Remember where they wanted to go, so the login page can send them back afterwards
  return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};
