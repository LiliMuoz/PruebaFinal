import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, LoginRequest } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card card fade-in">
        <div class="auth-header">
          <div class="auth-logo">🏦</div>
          <h1>CoopCredit</h1>
          <p>Sistema de Gestión de Créditos</p>
        </div>
        
        <form (ngSubmit)="onSubmit()" class="auth-form">
          <div class="alert alert-error" *ngIf="error">{{ error }}</div>
          
          <div class="form-group">
            <label for="username">Usuario</label>
            <input 
              type="text" 
              id="username" 
              class="form-control" 
              [(ngModel)]="credentials.username" 
              name="username"
              placeholder="Ingrese su usuario"
              required>
          </div>
          
          <div class="form-group">
            <label for="password">Contraseña</label>
            <input 
              type="password" 
              id="password" 
              class="form-control" 
              [(ngModel)]="credentials.password" 
              name="password"
              placeholder="Ingrese su contraseña"
              required>
          </div>
          
          <button type="submit" class="btn btn-primary btn-block" [disabled]="loading">
            <span class="loader" *ngIf="loading"></span>
            {{ loading ? 'Ingresando...' : 'Iniciar Sesión' }}
          </button>
        </form>
        
        <div class="auth-footer">
          <p>¿No tienes cuenta? <a routerLink="/register">Regístrate aquí</a></p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 2rem;
      background: linear-gradient(135deg, #0d3d4d 0%, #1a5f7a 50%, #2d8ba8 100%);
    }
    
    .auth-card {
      width: 100%;
      max-width: 420px;
      padding: 2.5rem;
    }
    
    .auth-header {
      text-align: center;
      margin-bottom: 2rem;
    }
    
    .auth-logo {
      font-size: 4rem;
      margin-bottom: 1rem;
    }
    
    .auth-header h1 {
      font-size: 2rem;
      font-weight: 700;
      color: var(--primary-dark);
      margin-bottom: 0.5rem;
    }
    
    .auth-header p {
      color: var(--text-secondary);
      font-size: 0.95rem;
    }
    
    .auth-form {
      margin-bottom: 1.5rem;
    }
    
    .btn-block {
      width: 100%;
      padding: 1rem;
      font-size: 1rem;
    }
    
    .auth-footer {
      text-align: center;
      color: var(--text-secondary);
      font-size: 0.9rem;
    }
    
    .auth-footer a {
      color: var(--primary-color);
      font-weight: 500;
      text-decoration: none;
    }
    
    .auth-footer a:hover {
      text-decoration: underline;
    }
  `]
})
export class LoginComponent {
  credentials: LoginRequest = { username: '', password: '' };
  loading = false;
  error = '';
  
  constructor(private authService: AuthService, private router: Router) {}
  
  onSubmit(): void {
    if (!this.credentials.username || !this.credentials.password) {
      this.error = 'Por favor complete todos los campos';
      return;
    }
    
    this.loading = true;
    this.error = '';
    
    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.router.navigate(['/credit/list']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.detail || 'Credenciales inválidas';
      }
    });
  }
}

