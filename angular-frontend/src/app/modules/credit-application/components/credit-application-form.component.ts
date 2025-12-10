import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CreditApplicationService, CreditApplicationRequest } from '../../../core/services/credit-application.service';
import { AffiliateService, AffiliateResponse } from '../../../core/services/affiliate.service';

@Component({
  selector: 'app-credit-application-form',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe, RouterLink],
  template: `
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">💳 Nueva Solicitud de Crédito</h1>
        <p class="page-subtitle">Complete la información para solicitar un crédito</p>
      </div>
      
      <div class="card fade-in">
        <!-- Estado de carga -->
        <div class="loading-state" *ngIf="checkingProfile">
          <span class="loader"></span>
          <p>Verificando perfil...</p>
        </div>
        
        <!-- Sin perfil de afiliado -->
        <div class="no-profile" *ngIf="!checkingProfile && !hasProfile">
          <div class="no-profile-icon">👤</div>
          <h3>Perfil Incompleto</h3>
          <p>Debe completar su perfil de afiliado antes de solicitar un crédito.</p>
          <a routerLink="/affiliate/new" class="btn btn-primary">Completar Perfil</a>
        </div>
        
        <!-- Formulario -->
        <form (ngSubmit)="onSubmit()" *ngIf="!checkingProfile && hasProfile">
          <div class="alert alert-error" *ngIf="error">{{ error }}</div>
          <div class="alert alert-success" *ngIf="success">{{ success }}</div>
          
          <div class="affiliate-info" *ngIf="affiliateProfile">
            <h3>👤 Solicitante</h3>
            <p><strong>{{ affiliateProfile.fullName }}</strong></p>
            <p>{{ affiliateProfile.documentType }}: {{ affiliateProfile.documentNumber }}</p>
          </div>
          
          <div class="grid grid-2">
            <div class="form-group">
              <label for="requestedAmount">Monto Solicitado</label>
              <input 
                type="number" 
                id="requestedAmount" 
                class="form-control" 
                [(ngModel)]="application.requestedAmount" 
                name="requestedAmount"
                placeholder="Min: 100,000 - Max: 50,000,000"
                required
                min="100000"
                max="50000000"
                (input)="calculatePayment()">
            </div>
            
            <div class="form-group">
              <label for="termMonths">Plazo (meses)</label>
              <select 
                id="termMonths" 
                class="form-control" 
                [(ngModel)]="application.termMonths" 
                name="termMonths"
                required
                (change)="calculatePayment()">
                <option value="">Seleccione el plazo</option>
                <option *ngFor="let term of availableTerms" [value]="term">{{ term }} meses</option>
              </select>
            </div>
            
            <div class="form-group full-width">
              <label for="purpose">Propósito del Crédito</label>
              <input 
                type="text" 
                id="purpose" 
                class="form-control" 
                [(ngModel)]="application.purpose" 
                name="purpose"
                placeholder="Ej: Compra de vehículo, mejoras del hogar...">
            </div>
          </div>
          
          <div class="summary-card" *ngIf="application.requestedAmount && application.termMonths">
            <h3>📊 Resumen de la Solicitud</h3>
            <div class="summary-grid">
              <div class="summary-item">
                <span class="label">Monto solicitado:</span>
                <span class="value">{{ application.requestedAmount | currency:'COP':'symbol-narrow':'1.0-0' }}</span>
              </div>
              <div class="summary-item">
                <span class="label">Plazo:</span>
                <span class="value">{{ application.termMonths }} meses</span>
              </div>
              <div class="summary-item">
                <span class="label">Tasa de interés:</span>
                <span class="value">12.5% E.A.</span>
              </div>
              <div class="summary-item highlight">
                <span class="label">Cuota estimada:</span>
                <span class="value">{{ estimatedPayment | currency:'COP':'symbol-narrow':'1.0-0' }}/mes</span>
              </div>
            </div>
          </div>
          
          <div class="form-actions">
            <button type="button" class="btn btn-secondary" (click)="goBack()">Cancelar</button>
            <button type="submit" class="btn btn-primary" [disabled]="loading">
              <span class="loader" *ngIf="loading"></span>
              {{ loading ? 'Enviando...' : 'Enviar Solicitud' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .loading-state {
      text-align: center;
      padding: 3rem;
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
    
    .no-profile {
      text-align: center;
      padding: 3rem;
    }
    
    .no-profile-icon {
      font-size: 4rem;
      margin-bottom: 1rem;
    }
    
    .no-profile h3 {
      color: var(--text-primary);
      margin-bottom: 0.5rem;
    }
    
    .no-profile p {
      color: var(--text-secondary);
      margin-bottom: 1.5rem;
    }
    
    .affiliate-info {
      background: rgba(26, 95, 122, 0.1);
      border-radius: var(--radius-md);
      padding: 1rem 1.5rem;
      margin-bottom: 1.5rem;
    }
    
    .affiliate-info h3 {
      font-size: 0.9rem;
      color: var(--primary-color);
      margin-bottom: 0.5rem;
    }
    
    .affiliate-info p {
      margin: 0.25rem 0;
      color: var(--text-primary);
    }
    
    .full-width {
      grid-column: span 2;
    }
    
    .summary-card {
      background: linear-gradient(135deg, var(--background-color), #e0f2fe);
      border-radius: var(--radius-md);
      padding: 1.5rem;
      margin-top: 1.5rem;
    }
    
    .summary-card h3 {
      font-size: 1.1rem;
      color: var(--primary-dark);
      margin-bottom: 1rem;
    }
    
    .summary-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 1rem;
    }
    
    .summary-item {
      display: flex;
      justify-content: space-between;
      padding: 0.75rem;
      background: white;
      border-radius: var(--radius-sm);
    }
    
    .summary-item .label {
      color: var(--text-secondary);
      font-size: 0.9rem;
    }
    
    .summary-item .value {
      font-weight: 600;
      color: var(--text-primary);
    }
    
    .summary-item.highlight {
      background: var(--primary-color);
      color: white;
    }
    
    .summary-item.highlight .label,
    .summary-item.highlight .value {
      color: white;
    }
    
    .form-actions {
      display: flex;
      justify-content: flex-end;
      gap: 1rem;
      margin-top: 2rem;
      padding-top: 1.5rem;
      border-top: 1px solid var(--border-color);
    }
    
    @media (max-width: 768px) {
      .summary-grid {
        grid-template-columns: 1fr;
      }
      .full-width {
        grid-column: span 1;
      }
    }
  `]
})
export class CreditApplicationFormComponent implements OnInit {
  application: CreditApplicationRequest = {
    requestedAmount: 0,
    termMonths: 0,
    purpose: ''
  };
  
  availableTerms = [6, 12, 18, 24, 36, 48, 60];
  estimatedPayment = 0;
  loading = false;
  checkingProfile = true;
  hasProfile = false;
  affiliateProfile: AffiliateResponse | null = null;
  error = '';
  success = '';
  
  constructor(
    private creditService: CreditApplicationService,
    private affiliateService: AffiliateService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.checkAffiliateProfile();
  }
  
  checkAffiliateProfile(): void {
    this.affiliateService.getMyAffiliate().subscribe({
      next: (profile) => {
        this.checkingProfile = false;
        if (profile) {
          this.hasProfile = true;
          this.affiliateProfile = profile;
        }
      },
      error: () => {
        this.checkingProfile = false;
        this.hasProfile = false;
      }
    });
  }
  
  calculatePayment(): void {
    if (this.application.requestedAmount && this.application.termMonths) {
      const principal = this.application.requestedAmount;
      const monthlyRate = 12.5 / 100 / 12;
      const n = this.application.termMonths;
      
      if (monthlyRate > 0) {
        this.estimatedPayment = principal * (monthlyRate * Math.pow(1 + monthlyRate, n)) / 
                               (Math.pow(1 + monthlyRate, n) - 1);
      } else {
        this.estimatedPayment = principal / n;
      }
    }
  }
  
  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }
    
    this.loading = true;
    this.error = '';
    this.success = '';
    
    this.creditService.createApplication(this.application).subscribe({
      next: () => {
        this.success = '¡Solicitud enviada exitosamente! Su solicitud será evaluada pronto.';
        setTimeout(() => this.router.navigate(['/credit/list']), 2000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.detail || 'Error al enviar la solicitud';
      }
    });
  }
  
  validateForm(): boolean {
    if (!this.application.requestedAmount || this.application.requestedAmount < 100000) {
      this.error = 'El monto mínimo es de $100,000';
      return false;
    }
    
    if (this.application.requestedAmount > 50000000) {
      this.error = 'El monto máximo es de $50,000,000';
      return false;
    }
    
    if (!this.application.termMonths) {
      this.error = 'Por favor seleccione el plazo';
      return false;
    }
    
    return true;
  }
  
  goBack(): void {
    this.router.navigate(['/credit/list']);
  }
}

