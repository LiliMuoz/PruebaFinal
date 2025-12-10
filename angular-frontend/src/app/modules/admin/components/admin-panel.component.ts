import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { AuthService, User } from '../../../core/services/auth.service';
import { AffiliateResponse } from '../../../core/services/affiliate.service';

interface UserWithRole extends User {
  newRole?: string;
}

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">⚙️ Panel de Administración</h1>
        <p class="page-subtitle">Gestión de usuarios, roles y afiliados</p>
      </div>
      
      <div class="alert alert-error" *ngIf="error">{{ error }}</div>
      <div class="alert alert-success" *ngIf="success">{{ success }}</div>
      
      <!-- Tabs -->
      <div class="tabs">
        <button 
          class="tab" 
          [class.active]="activeTab === 'users'" 
          (click)="activeTab = 'users'">
          👥 Usuarios ({{ users.length }})
        </button>
        <button 
          class="tab" 
          [class.active]="activeTab === 'affiliates'" 
          (click)="activeTab = 'affiliates'">
          📋 Afiliados ({{ affiliates.length }})
        </button>
      </div>
      
      <!-- Users Tab -->
      <div class="card fade-in" *ngIf="activeTab === 'users'">
        <div class="card-header">
          <h2>Gestión de Usuarios</h2>
          <p>Asigna roles a los usuarios del sistema</p>
        </div>
        
        <div class="loading-state" *ngIf="loadingUsers">
          <span class="loader"></span>
          <p>Cargando usuarios...</p>
        </div>
        
        <div class="table-container" *ngIf="!loadingUsers && users.length > 0">
          <table>
            <thead>
              <tr>
                <th>Usuario</th>
                <th>Email</th>
                <th>Rol Actual</th>
                <th>Estado</th>
                <th>Nuevo Rol</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let user of users">
                <td><strong>{{ user.username }}</strong></td>
                <td>{{ user.email }}</td>
                <td>
                  <span class="badge" [class]="getRoleBadgeClass(user.role)">
                    {{ user.role }}
                  </span>
                </td>
                <td>
                  <span class="badge" [class.badge-approved]="user.active" [class.badge-rejected]="!user.active">
                    {{ user.active ? 'Activo' : 'Inactivo' }}
                  </span>
                </td>
                <td>
                  <select 
                    class="form-control form-control-sm" 
                    [(ngModel)]="user.newRole"
                    [disabled]="processingUser === user.username">
                    <option value="">Sin cambio</option>
                    <option value="AFILIADO">AFILIADO</option>
                    <option value="ANALISTA">ANALISTA</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </td>
                <td>
                  <button 
                    class="btn btn-sm btn-primary" 
                    [disabled]="!user.newRole || processingUser === user.username"
                    (click)="changeRole(user)">
                    <span class="loader-sm" *ngIf="processingUser === user.username"></span>
                    {{ processingUser === user.username ? '' : '💾 Guardar' }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <div class="empty-state" *ngIf="!loadingUsers && users.length === 0">
          <div class="empty-icon">👥</div>
          <h3>Sin usuarios</h3>
          <p>No hay usuarios registrados en el sistema</p>
        </div>
      </div>
      
      <!-- Affiliates Tab -->
      <div class="card fade-in" *ngIf="activeTab === 'affiliates'">
        <div class="card-header">
          <h2>Afiliados Registrados</h2>
          <p>Lista de perfiles de afiliados en el sistema</p>
        </div>
        
        <div class="loading-state" *ngIf="loadingAffiliates">
          <span class="loader"></span>
          <p>Cargando afiliados...</p>
        </div>
        
        <div class="table-container" *ngIf="!loadingAffiliates && affiliates.length > 0">
          <table>
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Documento</th>
                <th>Email</th>
                <th>Teléfono</th>
                <th>Fecha Registro</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let affiliate of affiliates">
                <td><strong>{{ affiliate.fullName }}</strong></td>
                <td>{{ affiliate.documentType }}: {{ affiliate.documentNumber }}</td>
                <td>{{ affiliate.email }}</td>
                <td>{{ affiliate.phone || '-' }}</td>
                <td>{{ affiliate.createdAt | date:'dd/MM/yyyy' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <div class="empty-state" *ngIf="!loadingAffiliates && affiliates.length === 0">
          <div class="empty-icon">📋</div>
          <h3>Sin afiliados</h3>
          <p>No hay perfiles de afiliados registrados</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .tabs {
      display: flex;
      gap: 0.5rem;
      margin-bottom: 1.5rem;
    }
    
    .tab {
      padding: 0.75rem 1.5rem;
      border: none;
      background: var(--surface-color);
      color: var(--text-secondary);
      border-radius: var(--radius-md);
      cursor: pointer;
      font-weight: 500;
      transition: all 0.2s ease;
    }
    
    .tab:hover {
      background: var(--primary-light);
      color: var(--primary-dark);
    }
    
    .tab.active {
      background: var(--primary-color);
      color: white;
    }
    
    .card-header {
      margin-bottom: 1.5rem;
      padding-bottom: 1rem;
      border-bottom: 1px solid var(--border-color);
    }
    
    .card-header h2 {
      font-size: 1.25rem;
      color: var(--primary-dark);
      margin-bottom: 0.25rem;
    }
    
    .card-header p {
      color: var(--text-secondary);
      font-size: 0.9rem;
    }
    
    .form-control-sm {
      padding: 0.4rem 0.75rem;
      font-size: 0.85rem;
    }
    
    .btn-sm {
      padding: 0.4rem 0.75rem;
      font-size: 0.8rem;
    }
    
    .loader-sm {
      width: 16px;
      height: 16px;
      border-width: 2px;
    }
    
    .badge-afiliado {
      background: rgba(87, 197, 182, 0.2);
      color: #0d9488;
    }
    
    .badge-analista {
      background: rgba(245, 158, 11, 0.2);
      color: #d97706;
    }
    
    .badge-admin {
      background: rgba(239, 68, 68, 0.2);
      color: #dc2626;
    }
    
    .badge-approved {
      background: rgba(16, 185, 129, 0.2);
      color: #059669;
    }
    
    .badge-rejected {
      background: rgba(239, 68, 68, 0.2);
      color: #dc2626;
    }
    
    .empty-state, .loading-state {
      text-align: center;
      padding: 3rem;
    }
    
    .empty-icon {
      font-size: 4rem;
      margin-bottom: 1rem;
    }
    
    .empty-state h3 {
      color: var(--text-primary);
      margin-bottom: 0.5rem;
    }
    
    .empty-state p {
      color: var(--text-secondary);
    }
    
    .loading-state .loader {
      width: 40px;
      height: 40px;
      border-width: 3px;
      margin: 0 auto 1rem;
      display: block;
    }
  `]
})
export class AdminPanelComponent implements OnInit {
  activeTab: 'users' | 'affiliates' = 'users';
  users: UserWithRole[] = [];
  affiliates: AffiliateResponse[] = [];
  loadingUsers = true;
  loadingAffiliates = true;
  processingUser: string | null = null;
  error = '';
  success = '';
  
  private apiUrl = environment.apiUrl;
  
  constructor(
    private http: HttpClient,
    public authService: AuthService
  ) {}
  
  ngOnInit(): void {
    this.loadUsers();
    this.loadAffiliates();
  }
  
  loadUsers(): void {
    this.loadingUsers = true;
    this.http.get<User[]>(`${this.apiUrl}/auth/users`).subscribe({
      next: (users) => {
        this.users = users.map(u => ({ ...u, newRole: '' }));
        this.loadingUsers = false;
      },
      error: (err) => {
        this.error = err.error?.detail || 'Error al cargar usuarios';
        this.loadingUsers = false;
      }
    });
  }
  
  loadAffiliates(): void {
    this.loadingAffiliates = true;
    this.http.get<AffiliateResponse[]>(`${this.apiUrl}/affiliates`).subscribe({
      next: (affiliates) => {
        this.affiliates = affiliates;
        this.loadingAffiliates = false;
      },
      error: (err) => {
        this.error = err.error?.detail || 'Error al cargar afiliados';
        this.loadingAffiliates = false;
      }
    });
  }
  
  changeRole(user: UserWithRole): void {
    if (!user.newRole) return;
    
    this.processingUser = user.username;
    this.error = '';
    this.success = '';
    
    this.http.put<User>(`${this.apiUrl}/auth/users/${user.username}/role`, { 
      username: user.username,
      role: user.newRole 
    }).subscribe({
      next: (updatedUser) => {
        user.role = updatedUser.role;
        user.newRole = '';
        this.processingUser = null;
        this.success = `Rol de ${user.username} actualizado a ${updatedUser.role}`;
        setTimeout(() => this.success = '', 3000);
      },
      error: (err) => {
        this.error = err.error?.detail || 'Error al cambiar el rol';
        this.processingUser = null;
      }
    });
  }
  
  getRoleBadgeClass(role: string): string {
    return `badge badge-${role.toLowerCase()}`;
  }
}

