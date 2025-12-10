import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  template: `
    <div class="app-container">
      <nav class="navbar" *ngIf="authService.isAuthenticated()">
        <div class="nav-brand">
          <span class="logo">🏦</span>
          <span class="brand-text">CoopCredit</span>
        </div>
        <div class="nav-links">
          <a routerLink="/credit/list" class="nav-link">📋 Solicitudes</a>
          <a routerLink="/credit/new" class="nav-link" *ngIf="authService.hasRole('AFILIADO')">➕ Nueva Solicitud</a>
          <a routerLink="/affiliate/new" class="nav-link" *ngIf="authService.hasRole('AFILIADO')">👤 Mi Perfil</a>
          <a routerLink="/admin" class="nav-link" *ngIf="authService.hasRole('ADMIN')">⚙️ Administración</a>
        </div>
        <div class="nav-user">
          <span class="user-info">
            <span class="user-role badge" [class]="getRoleBadgeClass()">{{ authService.getRole() }}</span>
            {{ authService.getUsername() }}
          </span>
          <button class="btn btn-secondary btn-sm" (click)="logout()">Cerrar Sesión</button>
        </div>
      </nav>
      
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
      
      <footer class="footer" *ngIf="authService.isAuthenticated()">
        <p>© 2024 CoopCredit - Sistema de Gestión de Créditos</p>
      </footer>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }
    
    .navbar {
      background: linear-gradient(135deg, var(--primary-dark), var(--primary-color));
      color: white;
      padding: 1rem 2rem;
      display: flex;
      align-items: center;
      justify-content: space-between;
      box-shadow: var(--shadow-lg);
      position: sticky;
      top: 0;
      z-index: 100;
    }
    
    .nav-brand {
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }
    
    .logo {
      font-size: 1.75rem;
    }
    
    .brand-text {
      font-size: 1.5rem;
      font-weight: 700;
      letter-spacing: -0.02em;
    }
    
    .nav-links {
      display: flex;
      gap: 0.5rem;
    }
    
    .nav-link {
      color: rgba(255,255,255,0.9);
      text-decoration: none;
      padding: 0.6rem 1.2rem;
      border-radius: var(--radius-md);
      font-weight: 500;
      transition: all 0.2s ease;
    }
    
    .nav-link:hover {
      background: rgba(255,255,255,0.15);
      color: white;
    }
    
    .nav-user {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    
    .user-info {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      font-weight: 500;
    }
    
    .user-role {
      font-size: 0.7rem;
      padding: 0.25rem 0.6rem;
    }
    
    .badge-afiliado {
      background: rgba(87, 197, 182, 0.3);
      color: #10b981;
    }
    
    .badge-analista {
      background: rgba(245, 158, 11, 0.3);
      color: #f59e0b;
    }
    
    .badge-admin {
      background: rgba(239, 68, 68, 0.3);
      color: #fca5a5;
    }
    
    .btn-sm {
      padding: 0.5rem 1rem;
      font-size: 0.85rem;
    }
    
    .main-content {
      flex: 1;
      padding: 2rem;
    }
    
    .footer {
      background: var(--primary-dark);
      color: rgba(255,255,255,0.7);
      text-align: center;
      padding: 1.25rem;
      font-size: 0.9rem;
    }
  `]
})
export class AppComponent {
  constructor(public authService: AuthService, private router: Router) {}
  
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
  
  getRoleBadgeClass(): string {
    const role = this.authService.getRole()?.toLowerCase() || '';
    return `badge badge-${role}`;
  }
}

