import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, RegisterRequest } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card card fade-in">
        <div class="auth-header">
          <div class="auth-logo">🏦</div>
          <h1>Crear Cuenta</h1>
          <p>Regístrate en CoopCredit</p>
        </div>
        
        <form (ngSubmit)="onSubmit()" class="auth-form">
          <div class="alert alert-error" *ngIf="error">{{ error }}</div>
          <div class="alert alert-success" *ngIf="success">{{ success }}</div>
          
          <div class="form-group">
            <label for="username">Usuario</label>
            <input 
              type="text" 
              id="username" 
              class="form-control" 
              [(ngModel)]="user.username" 
              name="username"
              placeholder="Nombre de usuario"
              required
              minlength="3">
          </div>
          
          <div class="form-group">
            <label for="email">Email</label>
            <input 
              type="email" 
              id="email" 
              class="form-control" 
              [(ngModel)]="user.email" 
              name="email"
              placeholder="correo@ejemplo.com"
              required>
          </div>
          
          <div class="form-group">
            <label for="password">Contraseña</label>
            <input 
              type="password" 
              id="password" 
              class="form-control" 
              [(ngModel)]="user.password" 
              name="password"
              placeholder="Mínimo 6 caracteres"
              required
              minlength="6">
          </div>
          
          <div class="info-box">
            <span class="info-icon">ℹ️</span>
            <span>Tu cuenta será creada como <strong>Afiliado</strong>. Un administrador podrá asignarte otro rol si es necesario.</span>
          </div>
          
          <button type="submit" class="btn btn-primary btn-block" [disabled]="loading">
            <span class="loader" *ngIf="loading"></span>
            {{ loading ? 'Registrando...' : 'Crear Cuenta' }}
          </button>
        </form>
        
        <div class="auth-footer">
          <p>¿Ya tienes cuenta? <a routerLink="/login">Inicia sesión</a></p>
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
      font-size: 1.75rem;
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
    
    .info-box {
      background: rgba(26, 95, 122, 0.1);
      border: 1px solid rgba(26, 95, 122, 0.3);
      border-radius: var(--radius-md);
      padding: 0.75rem 1rem;
      margin-bottom: 1.5rem;
      display: flex;
      align-items: flex-start;
      gap: 0.5rem;
      font-size: 0.85rem;
      color: var(--text-secondary);
    }
    
    .info-icon {
      flex-shrink: 0;
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
export class RegisterComponent {
  user: RegisterRequest = { username: '', password: '', email: '' };
  loading = false;
  error = '';
  success = '';
  
  constructor(private authService: AuthService, private router: Router) {}
  
  onSubmit(): void {
    if (!this.user.username || !this.user.password || !this.user.email) {
      this.error = 'Por favor complete todos los campos';
      return;
    }
    
    if (this.user.password.length < 6) {
      this.error = 'La contraseña debe tener al menos 6 caracteres';
      return;
    }
    
    this.loading = true;
    this.error = '';
    this.success = '';
    
    this.authService.register(this.user).subscribe({
      next: () => {
        this.success = '¡Cuenta creada exitosamente! Redirigiendo...';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.detail || 'Error al crear la cuenta';
      }
    });
  }
}

