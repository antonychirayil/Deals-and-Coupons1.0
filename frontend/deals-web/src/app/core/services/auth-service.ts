import { HttpClient } from '@angular/common/http';
import { computed, inject, Service, signal } from '@angular/core';
import { Observable, switchMap, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, RegisterRequest, User } from '../models/auth';

const STORAGE_KEY = 'deals-session';

// What we keep in localStorage so the user stays logged in after a page refresh
interface Session {
  token: string;
  expiresAt: number; // milliseconds since 1970, like Date.now()
  user: User;
}

/**
 * Knows who is logged in. Components read the signals below; only this class changes them.
 */
@Service()
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  private readonly session = signal<Session | null>(loadSession());

  readonly currentUser = computed(() => this.session()?.user ?? null);
  readonly isLoggedIn = computed(() => this.session() !== null);
  readonly isAdmin = computed(() => this.currentUser()?.role === 'ADMIN');

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, request).pipe(
      // tap() lets us do something with the response without changing it
      tap((response) => this.startSession(response)),
    );
  }

  register(request: RegisterRequest): Observable<LoginResponse> {
    // Create the account, THEN log in with the same email and password.
    // switchMap() = "when the first request finishes, start this second one".
    return this.http
      .post<User>(`${this.baseUrl}/register`, request)
      .pipe(switchMap(() => this.login({ email: request.email, password: request.password })));
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.session.set(null);
  }

  getToken(): string | null {
    return this.session()?.token ?? null;
  }

  private startSession(response: LoginResponse): void {
    const session: Session = {
      token: response.token,
      expiresAt: Date.now() + response.expiresInSeconds * 1000,
      user: response.user,
    };
    // localStorage only stores text, so we convert the object to a JSON string
    localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
    this.session.set(session);
  }
}

// Runs once when the app starts: was someone already logged in?
function loadSession(): Session | null {
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (!saved) {
      return null;
    }
    const session = JSON.parse(saved) as Session;
    if (Date.now() > session.expiresAt) {
      localStorage.removeItem(STORAGE_KEY); // token has expired: start logged out
      return null;
    }
    return session;
  } catch {
    return null; // storage blocked or data corrupted: start logged out
  }
}
