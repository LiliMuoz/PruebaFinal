import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CreditApplicationService, CreditApplicationResponse } from '../../../core/services/credit-application.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-credit-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, CurrencyPipe, DatePipe],
  template: `
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="page-title">📋 Solicitudes de Crédito</h1>
          <p class="page-subtitle">Gestione las solicitudes de crédito del sistema</p>
        </div>
        <a routerLink="/credit/new" class="btn btn-primary" *ngIf="authService.hasRole('AFILIADO')">
          ➕ Nueva Solicitud
        </a>
      </div>
      
      <div class="alert alert-error" *ngIf="error">{{ error }}</div>
      <div class="alert alert-success" *ngIf="successMessage">{{ successMessage }}</div>
      
      <div class="card fade-in">
        <div class="table-container" *ngIf="!loading && applications.length > 0">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Afiliado</th>
                <th>Monto</th>
                <th>Plazo</th>
                <th>Cuota</th>
                <th>Estado</th>
                <th>Fecha</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let app of applications">
                <td><strong>#{{ app.id }}</strong></td>
                <td>{{ app.affiliateName }}</td>
                <td>{{ app.requestedAmount | currency:'COP':'symbol-narrow':'1.0-0' }}</td>
                <td>{{ app.termMonths }} meses</td>
                <td>{{ app.monthlyPayment | currency:'COP':'symbol-narrow':'1.0-0' }}</td>
                <td>
                  <span class="badge" [class]="getStatusClass(app.status)">
                    {{ getStatusLabel(app.status) }}
                  </span>
                </td>
                <td>{{ app.createdAt | date:'dd/MM/yyyy' }}</td>
                <td class="actions">
                  <button 
                    class="btn btn-sm btn-primary" 
                    *ngIf="canEvaluate(app)"
                    (click)="evaluate(app.id)"
                    [disabled]="processingId === app.id">
                    🔍 Evaluar
                  </button>
                  <button 
                    class="btn btn-sm btn-success" 
                    *ngIf="canApprove(app)"
                    (click)="approve(app.id)"
                    [disabled]="processingId === app.id">
                    ✅ Aprobar
                  </button>
                  <button 
                    class="btn btn-sm btn-danger" 
                    *ngIf="canReject(app)"
                    (click)="showRejectModal(app)"
                    [disabled]="processingId === app.id">
                    ❌ Rechazar
                  </button>
                  <button 
                    class="btn btn-sm btn-secondary" 
                    (click)="viewDetails(app)">
                    👁️ Ver
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <div class="empty-state" *ngIf="!loading && applications.length === 0">
          <div class="empty-icon">📭</div>
          <h3>No hay solicitudes</h3>
          <p>No se encontraron solicitudes de crédito</p>
          <a routerLink="/credit/new" class="btn btn-primary" *ngIf="authService.hasRole('AFILIADO')">
            Crear primera solicitud
          </a>
        </div>
        
        <div class="loading-state" *ngIf="loading">
          <span class="loader"></span>
          <p>Cargando solicitudes...</p>
        </div>
      </div>
      
      <!-- Modal de detalles -->
      <div class="modal-overlay" *ngIf="selectedApplication" (click)="closeModal()">
        <div class="modal-content card" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h2>Solicitud #{{ selectedApplication.id }}</h2>
            <button class="close-btn" (click)="closeModal()">✕</button>
          </div>
          <div class="modal-body">
            <div class="detail-grid">
              <div class="detail-item">
                <span class="label">Afiliado:</span>
                <span class="value">{{ selectedApplication.affiliateName }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Monto:</span>
                <span class="value">{{ selectedApplication.requestedAmount | currency:'COP':'symbol-narrow':'1.0-0' }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Plazo:</span>
                <span class="value">{{ selectedApplication.termMonths }} meses</span>
              </div>
              <div class="detail-item">
                <span class="label">Tasa:</span>
                <span class="value">{{ selectedApplication.interestRate }}%</span>
              </div>
              <div class="detail-item">
                <span class="label">Cuota mensual:</span>
                <span class="value">{{ selectedApplication.monthlyPayment | currency:'COP':'symbol-narrow':'1.0-0' }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Propósito:</span>
                <span class="value">{{ selectedApplication.purpose || 'No especificado' }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Estado:</span>
                <span class="badge" [class]="getStatusClass(selectedApplication.status)">
                  {{ getStatusLabel(selectedApplication.status) }}
                </span>
              </div>
              <div class="detail-item">
                <span class="label">Fecha solicitud:</span>
                <span class="value">{{ selectedApplication.createdAt | date:'dd/MM/yyyy HH:mm' }}</span>
              </div>
            </div>
            
            <div class="risk-evaluation" *ngIf="selectedApplication.riskEvaluation">
              <h3>📊 Evaluación de Riesgo</h3>
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="label">Score:</span>
                  <span class="value score">{{ selectedApplication.riskEvaluation.score }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">Nivel de riesgo:</span>
                  <span class="badge" [class]="getRiskClass(selectedApplication.riskEvaluation.riskLevel)">
                    {{ selectedApplication.riskEvaluation.riskLevel }}
                  </span>
                </div>
                <div class="detail-item full-width">
                  <span class="label">Recomendación:</span>
                  <span class="value">{{ selectedApplication.riskEvaluation.recommendation }}</span>
                </div>
              </div>
            </div>
            
            <div class="rejection-info" *ngIf="selectedApplication.rejectionReason">
              <h3>❌ Motivo de Rechazo</h3>
              <p>{{ selectedApplication.rejectionReason }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Modal de rechazo -->
      <div class="modal-overlay" *ngIf="showReject" (click)="closeRejectModal()">
        <div class="modal-content card" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h2>Rechazar Solicitud</h2>
            <button class="close-btn" (click)="closeRejectModal()">✕</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>Motivo del rechazo:</label>
              <textarea 
                class="form-control" 
                [(ngModel)]="rejectReason" 
                rows="4"
                placeholder="Ingrese el motivo del rechazo..."
                required></textarea>
            </div>
            <div class="modal-actions">
              <button class="btn btn-secondary" (click)="closeRejectModal()">Cancelar</button>
              <button 
                class="btn btn-danger" 
                (click)="confirmReject()"
                [disabled]="!rejectReason">
                Confirmar Rechazo
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .actions {
      display: flex;
      gap: 0.5rem;
      flex-wrap: wrap;
    }
    
    .btn-sm {
      padding: 0.4rem 0.75rem;
      font-size: 0.8rem;
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
      margin-bottom: 1.5rem;
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
    
    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0,0,0,0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
      padding: 1rem;
    }
    
    .modal-content {
      width: 100%;
      max-width: 600px;
      max-height: 90vh;
      overflow-y: auto;
    }
    
    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
      padding-bottom: 1rem;
      border-bottom: 1px solid var(--border-color);
    }
    
    .modal-header h2 {
      font-size: 1.25rem;
      color: var(--primary-dark);
    }
    
    .close-btn {
      background: none;
      border: none;
      font-size: 1.5rem;
      cursor: pointer;
      color: var(--text-secondary);
    }
    
    .detail-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 1rem;
    }
    
    .detail-item {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }
    
    .detail-item.full-width {
      grid-column: span 2;
    }
    
    .detail-item .label {
      font-size: 0.85rem;
      color: var(--text-secondary);
    }
    
    .detail-item .value {
      font-weight: 500;
      color: var(--text-primary);
    }
    
    .score {
      font-size: 1.5rem;
      font-weight: 700;
      color: var(--primary-color);
    }
    
    .risk-evaluation, .rejection-info {
      margin-top: 1.5rem;
      padding-top: 1.5rem;
      border-top: 1px solid var(--border-color);
    }
    
    .risk-evaluation h3, .rejection-info h3 {
      font-size: 1rem;
      color: var(--primary-dark);
      margin-bottom: 1rem;
    }
    
    .rejection-info p {
      background: rgba(239, 68, 68, 0.1);
      padding: 1rem;
      border-radius: var(--radius-md);
      color: #b91c1c;
    }
    
    .modal-actions {
      display: flex;
      justify-content: flex-end;
      gap: 1rem;
      margin-top: 1.5rem;
    }
    
    .badge-low {
      background: rgba(16, 185, 129, 0.15);
      color: #047857;
    }
    
    .badge-medium {
      background: rgba(245, 158, 11, 0.15);
      color: #b45309;
    }
    
    .badge-high {
      background: rgba(239, 68, 68, 0.15);
      color: #b91c1c;
    }
  `]
})
export class CreditListComponent implements OnInit {
  applications: CreditApplicationResponse[] = [];
  selectedApplication: CreditApplicationResponse | null = null;
  loading = true;
  error = '';
  successMessage = '';
  processingId: number | null = null;
  showReject = false;
  rejectReason = '';
  applicationToReject: CreditApplicationResponse | null = null;
  
  constructor(
    private creditService: CreditApplicationService,
    public authService: AuthService
  ) {}
  
  ngOnInit(): void {
    this.loadApplications();
  }
  
  loadApplications(): void {
    this.loading = true;
    this.error = '';
    
    // Los afiliados ven solo sus solicitudes, analistas/admins ven todas
    const request = this.authService.isAfiliado()
      ? this.creditService.getMyApplications()
      : this.creditService.getAllApplications();
    
    request.subscribe({
      next: (data) => {
        this.applications = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.detail || 'Error al cargar las solicitudes';
        this.loading = false;
      }
    });
  }
  
  getStatusClass(status: string): string {
    return `badge-${status.toLowerCase()}`;
  }
  
  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      'PENDING': 'Pendiente',
      'APPROVED': 'Aprobada',
      'REJECTED': 'Rechazada',
      'CANCELLED': 'Cancelada'
    };
    return labels[status] || status;
  }
  
  getRiskClass(level: string): string {
    return `badge-${level.toLowerCase()}`;
  }
  
  canEvaluate(app: CreditApplicationResponse): boolean {
    return app.status === 'PENDING' && this.authService.hasAnyRole(['ANALISTA', 'ADMIN']);
  }
  
  canApprove(app: CreditApplicationResponse): boolean {
    return app.status === 'PENDING' && this.authService.hasAnyRole(['ANALISTA', 'ADMIN']);
  }
  
  canReject(app: CreditApplicationResponse): boolean {
    return app.status === 'PENDING' && this.authService.hasAnyRole(['ANALISTA', 'ADMIN']);
  }
  
  evaluate(id: number): void {
    this.processingId = id;
    this.error = '';
    this.creditService.evaluateApplication(id).subscribe({
      next: (result) => {
        const statusMsg = result.status === 'APPROVED' ? '✅ APROBADA' : '❌ RECHAZADA';
        const scoreMsg = result.riskEvaluation ? ` (Score: ${result.riskEvaluation.score})` : '';
        this.successMessage = `Solicitud evaluada: ${statusMsg}${scoreMsg}`;
        this.loadApplications();
        this.processingId = null;
        setTimeout(() => this.successMessage = '', 5000);
      },
      error: (err) => {
        this.error = err.error?.detail || 'Error al evaluar la solicitud';
        this.processingId = null;
        setTimeout(() => this.error = '', 5000);
      }
    });
  }
  
  approve(id: number): void {
    this.processingId = id;
    this.creditService.approveApplication(id).subscribe({
      next: () => {
        this.successMessage = 'Solicitud aprobada exitosamente';
        this.loadApplications();
        this.processingId = null;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.error = err.error?.detail || 'Error al aprobar la solicitud';
        this.processingId = null;
      }
    });
  }
  
  showRejectModal(app: CreditApplicationResponse): void {
    this.applicationToReject = app;
    this.showReject = true;
    this.rejectReason = '';
  }
  
  closeRejectModal(): void {
    this.showReject = false;
    this.applicationToReject = null;
    this.rejectReason = '';
  }
  
  confirmReject(): void {
    if (!this.applicationToReject || !this.rejectReason) return;
    
    this.processingId = this.applicationToReject.id;
    this.creditService.rejectApplication(this.applicationToReject.id, this.rejectReason).subscribe({
      next: () => {
        this.successMessage = 'Solicitud rechazada';
        this.closeRejectModal();
        this.loadApplications();
        this.processingId = null;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.error = err.error?.detail || 'Error al rechazar la solicitud';
        this.processingId = null;
      }
    });
  }
  
  viewDetails(app: CreditApplicationResponse): void {
    this.selectedApplication = app;
  }
  
  closeModal(): void {
    this.selectedApplication = null;
  }
}

