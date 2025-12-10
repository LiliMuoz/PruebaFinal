import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  email: string;
  role: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  active: boolean;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  
  constructor(private http: HttpClient) {}
  
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify({
          username: response.username,
          email: response.email,
          role: response.role
        }));
      })
    );
  }
  
  register(data: RegisterRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/register`, data);
  }
  
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }
  
  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
  
  getToken(): string | null {
    return localStorage.getItem('token');
  }
  
  getUser(): { username: string; email: string; role: string } | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }
  
  getUsername(): string | null {
    return this.getUser()?.username || null;
  }
  
  getRole(): string | null {
    return this.getUser()?.role || null;
  }
  
  hasRole(role: string): boolean {
    return this.getRole() === role;
  }
  
  hasAnyRole(roles: string[]): boolean {
    const userRole = this.getRole();
    return userRole ? roles.includes(userRole) : false;
  }
  
  isAfiliado(): boolean {
    return this.hasRole('AFILIADO');
  }
  
  isAnalista(): boolean {
    return this.hasRole('ANALISTA');
  }
  
  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }
}

