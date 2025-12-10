import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  active: boolean;
  createdAt: string;
}

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="page-title">👥 Gestión de Usuarios</h1>
          <p class="page-subtitle">Administre los usuarios y asigne roles</p>
        </div>
      </div>
      
      <div class="alert alert-error" *ngIf="error">{{ error }}</div>
      <div class="alert alert-success" *ngIf="success">{{ success }}</div>
      
      <div class="card fade-in">
        <div class="table-container" *ngIf="!loading && users.length > 0">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Usuario</th>
                <th>Email</th>
                <th>Rol Actual</th>
                <th>Estado</th>
                <th>Fecha Registro</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let user of users">
                <td><strong>#{{ user.id }}</strong></td>
                <td>{{ user.username }}</td>
                <td>{{ user.email }}</td>
                <td>
                  <span class="badge" [class]="getRoleBadgeClass(user.role)">
                    {{ getRoleLabel(user.role) }}
                  </span>
                </td>
                <td>
                  <span class="badge" [class]="user.active ? 'badge-approved' : 'badge-rejected'">
                    {{ user.active ? 'Activo' : 'Inactivo' }}
                  </span>
                </td>
                <td>{{ user.createdAt | date:'dd/MM/yyyy' }}</td>
                <td class="actions">
                  <select 
                    class="role-select" 
                    [value]="user.role"
                    (change)="changeRole(user.username, $event)"
                    [disabled]="processingUser === user.username">
                    <option value="AFILIADO">Afiliado</option>
                    <option value="ANALISTA">Analista</option>
                    <option value="ADMIN">Administrador</option>
                  </select>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <div class="empty-state" *ngIf="!loading && users.length === 0">
          <div class="empty-icon">👥</div>
          <h3>No hay usuarios</h3>
          <p>No se encontraron usuarios registrados</p>
        </div>
        
        <div class="loading-state" *ngIf="loading">
          <span class="loader"></span>
          <p>Cargando usuarios...</p>
        </div>
      </div>
      
      <div class="info-card">
        <h3>ℹ️ Roles del Sistema</h3>
        <div class="roles-info">
          <div class="role-item">
            <span class="badge badge-afiliado">AFILIADO</span>
            <p>Puede crear solicitudes de crédito y ver su historial</p>
          </div>
          <div class="role-item">
            <span class="badge badge-analista">ANALISTA</span>
            <p>Puede evaluar, aprobar o rechazar solicitudes de crédito</p>
          </div>
          <div class="role-item">
            <span class="badge badge-admin">ADMIN</span>
            <p>Acceso completo al sistema, incluida la gestión de usuarios</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .actions {
      min-width: 150px;
    }
    
    .role-select {
      padding: 0.5rem;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-sm);
      background: white;
      font-size: 0.85rem;
      cursor: pointer;
    }
    
    .role-select:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
    
    .empty-state, .loading-state {
      text-align: center;
      padding: 3rem;
    }
    
    .empty-icon {
      font-size: 4rem;
      margin-bottom: 1rem;
    }
    
    .loading-state .loader {
      width: 40px;
      height: 40px;
      border-width: 3px;
      border-color: var(--primary-light);
      border-top-color: var(--primary-color);
      margin: 0 auto 1rem;
      display: block;
    }
    
    .info-card {
      margin-top: 2rem;
      padding: 1.5rem;
      background: rgba(26, 95, 122, 0.05);
      border: 1px solid rgba(26, 95, 122, 0.2);
      border-radius: var(--radius-md);
    }
    
    .info-card h3 {
      font-size: 1rem;
      color: var(--primary-dark);
      margin-bottom: 1rem;
    }
    
    .roles-info {
      display: grid;
      gap: 1rem;
    }
    
    .role-item {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    
    .role-item .badge {
      min-width: 100px;
      text-align: center;
    }
    
    .role-item p {
      margin: 0;
      color: var(--text-secondary);
      font-size: 0.9rem;
    }
    
    .badge-afiliado {
      background: rgba(87, 197, 182, 0.2);
      color: #0d9488;
    }
    
    .badge-analista {
      background: rgba(245, 158, 11, 0.2);
      color: #b45309;
    }
    
    .badge-admin {
      background: rgba(239, 68, 68, 0.2);
      color: #dc2626;
    }
  `]
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  loading = true;
  error = '';
  success = '';
  processingUser: string | null = null;
  
  private apiUrl = `${environment.apiUrl}/auth`;
  
  constructor(private http: HttpClient) {}
  
  ngOnInit(): void {
    this.loadUsers();
  }
  
  loadUsers(): void {
    this.loading = true;
    this.error = '';
    
    this.http.get<User[]>(`${this.apiUrl}/users`).subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.detail || 'Error al cargar los usuarios';
        this.loading = false;
      }
    });
  }
  
  changeRole(username: string, event: Event): void {
    const select = event.target as HTMLSelectElement;
    const newRole = select.value;
    
    this.processingUser = username;
    this.error = '';
    this.success = '';
    
    this.http.put<User>(`${this.apiUrl}/users/${username}/role`, { role: newRole }).subscribe({
      next: () => {
        this.success = `Rol de ${username} actualizado a ${this.getRoleLabel(newRole)}`;
        this.processingUser = null;
        this.loadUsers();
        setTimeout(() => this.success = '', 3000);
      },
      error: (err) => {
        this.error = err.error?.detail || 'Error al cambiar el rol';
        this.processingUser = null;
        // Recargar para restaurar el valor original
        this.loadUsers();
      }
    });
  }
  
  getRoleBadgeClass(role: string): string {
    return `badge-${role.toLowerCase()}`;
  }
  
  getRoleLabel(role: string): string {
    const labels: Record<string, string> = {
      'AFILIADO': 'Afiliado',
      'ANALISTA': 'Analista',
      'ADMIN': 'Administrador'
    };
    return labels[role] || role;
  }
}

