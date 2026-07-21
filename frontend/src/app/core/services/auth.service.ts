import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, finalize, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResult, AuthUser, LoginRequest } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly TOKEN_KEY = 'tm_token';
  private readonly USER_KEY = 'tm_user';

  private http = inject(HttpClient);
  private router = inject(Router);

  readonly currentUser = signal<AuthUser | null>(this.loadUser());
  readonly isAdmin = computed(() => this.currentUser()?.role === 'ADMIN');

  login(request: LoginRequest): Observable<AuthResult> {
    return this.http.post<AuthResult>(`${environment.apiUrl}/auth/login`, request)
      .pipe(tap(result => this.storeSession(result)));
  }

  logout(): void {
    this.http.post(`${environment.apiUrl}/auth/logout`, {})
      .pipe(finalize(() => this.clearSession()))
      .subscribe({ error: () => {} });
  }

  forceLogout(): void {
    this.clearSession();
  }

  updateName(name: string): void {
    const user = this.currentUser();
    if (user) {
      const updated = { ...user, name };
      localStorage.setItem(this.USER_KEY, JSON.stringify(updated));
      this.currentUser.set(updated);
    }
  }

  get token(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return this.token !== null;
  }

  private storeSession(result: AuthResult): void {
    const user: AuthUser = {
      id: result.userId,
      name: result.name,
      username: result.username,
      role: result.role
    };
    localStorage.setItem(this.TOKEN_KEY, result.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.currentUser.set(user);
  }

  private clearSession(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  private loadUser(): AuthUser | null {
    const raw = localStorage.getItem(this.USER_KEY);
    return raw ? JSON.parse(raw) as AuthUser : null;
  }
}
