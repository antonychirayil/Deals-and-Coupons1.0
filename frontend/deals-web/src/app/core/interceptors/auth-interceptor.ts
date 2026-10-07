import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthService } from '../services/auth-service';

/**
 * Runs for EVERY HttpClient request, so no service has to add the token by hand.
 */
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  // Only send our token to OUR backend, never to other websites
  const isApiRequest = request.url.startsWith(environment.apiUrl);
  if (!token || !isApiRequest) {
    return next(request);
  }

  // Requests can't be changed, so we make a copy with the extra header
  const requestWithToken = request.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  });

  return next(requestWithToken).pipe(
    catchError((error: HttpErrorResponse) => {
      // 401 although we sent a token = the token expired or is invalid: log out and ask to log in again
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login'], { queryParams: { returnUrl: router.url } });
      }
      return throwError(() => error); // still let the component see the error
    }),
  );
};
