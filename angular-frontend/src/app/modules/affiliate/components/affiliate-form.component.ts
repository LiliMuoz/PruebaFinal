import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AffiliateService, AffiliateRequest, AffiliateResponse } from '../../../core/services/affiliate.service';

@Component({
  selector: 'app-affiliate-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">👤 {{ isEditing ? 'Mi Perfil de Afiliado' : 'Completar Perfil de Afiliado' }}</h1>
        <p class="page-subtitle">{{ isEditing ? 'Actualice sus datos personales' : 'Complete sus datos para poder solicitar créditos' }}</p>
      </div>
      
      <div class="card fade-in">
        <div class="loading-container" *ngIf="loadingProfile">
          <span class="loader"></span>
          <p>Cargando perfil...</p>
        </div>
        
        <form (ngSubmit)="onSubmit()" *ngIf="!loadingProfile">
          <div class="alert alert-error" *ngIf="error">{{ error }}</div>
          <div class="alert alert-success" *ngIf="success">{{ success }}</div>
          
          <div class="grid grid-2">
            <div class="form-group">
              <label for="documentType">Tipo de Documento</label>
              <select 
                id="documentType" 
                class="form-control" 
                [(ngModel)]="affiliate.documentType" 
                name="documentType"
                [disabled]="isEditing"
                required>
                <option value="">Seleccione</option>
                <option value="CC">Cédula de Ciudadanía</option>
                <option value="CE">Cédula de Extranjería</option>
                <option value="NIT">NIT</option>
                <option value="PASAPORTE">Pasaporte</option>
              </select>
            </div>
            
            <div class="form-group">
              <label for="documentNumber">Número de Documento</label>
              <input 
                type="text" 
                id="documentNumber" 
                class="form-control" 
                [(ngModel)]="affiliate.documentNumber" 
                name="documentNumber"
                placeholder="Ej: 1234567890"
                [readonly]="isEditing"
                required>
            </div>
            
            <div class="form-group">
              <label for="firstName">Nombres</label>
              <input 
                type="text" 
                id="firstName" 
                class="form-control" 
                [(ngModel)]="affiliate.firstName" 
                name="firstName"
                placeholder="Ingrese sus nombres"
                required>
            </div>
            
            <div class="form-group">
              <label for="lastName">Apellidos</label>
              <input 
                type="text" 
                id="lastName" 
                class="form-control" 
                [(ngModel)]="affiliate.lastName" 
                name="lastName"
                placeholder="Ingrese sus apellidos"
                required>
            </div>
            
            <div class="form-group">
              <label for="email">Correo Electrónico</label>
              <input 
                type="email" 
                id="email" 
                class="form-control" 
                [(ngModel)]="affiliate.email" 
                name="email"
                placeholder="correo@ejemplo.com"
                [readonly]="isEditing"
                required>
            </div>
            
            <div class="form-group">
              <label for="phone">Teléfono</label>
              <input 
                type="text" 
                id="phone" 
                class="form-control" 
                [(ngModel)]="affiliate.phone" 
                name="phone"
                placeholder="Ej: 3001234567">
            </div>
            
            <div class="form-group">
              <label for="birthDate">Fecha de Nacimiento</label>
              <input 
                type="date" 
                id="birthDate" 
                class="form-control" 
                [(ngModel)]="affiliate.birthDate" 
                name="birthDate"
                [readonly]="isEditing"
                required>
            </div>
            
            <div class="form-group">
              <label for="address">Dirección</label>
              <input 
                type="text" 
                id="address" 
                class="form-control" 
                [(ngModel)]="affiliate.address" 
                name="address"
                placeholder="Dirección completa">
            </div>
          </div>
          
          <div class="form-actions">
            <button type="button" class="btn btn-secondary" (click)="goBack()">Cancelar</button>
            <button type="submit" class="btn btn-primary" [disabled]="loading">
              <span class="loader" *ngIf="loading"></span>
              {{ loading ? 'Guardando...' : (isEditing ? 'Actualizar Perfil' : 'Guardar Perfil') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .form-actions {
      display: flex;
      justify-content: flex-end;
      gap: 1rem;
      margin-top: 2rem;
      padding-top: 1.5rem;
      border-top: 1px solid var(--border-color);
    }
    
    .loading-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 3rem;
      color: var(--text-secondary);
    }
    
    input[readonly], select[disabled] {
      background-color: var(--surface-color);
      cursor: not-allowed;
    }
  `]
})
export class AffiliateFormComponent implements OnInit {
  affiliate: AffiliateRequest = {
    documentNumber: '',
    documentType: '',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    address: '',
    birthDate: ''
  };
  loading = false;
  loadingProfile = true;
  error = '';
  success = '';
  isEditing = false;
  
  constructor(
    private affiliateService: AffiliateService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.loadMyProfile();
  }
  
  loadMyProfile(): void {
    this.affiliateService.getMyAffiliate().subscribe({
      next: (existing) => {
        this.loadingProfile = false;
        if (existing) {
          this.isEditing = true;
          this.affiliate = {
            documentNumber: existing.documentNumber,
            documentType: existing.documentType,
            firstName: existing.firstName,
            lastName: existing.lastName,
            email: existing.email,
            phone: existing.phone || '',
            address: existing.address || '',
            birthDate: existing.birthDate
          };
        }
      },
      error: () => {
        this.loadingProfile = false;
      }
    });
  }
  
  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }
    
    this.loading = true;
    this.error = '';
    this.success = '';
    
    const operation = this.isEditing 
      ? this.affiliateService.updateMyAffiliate(this.affiliate)
      : this.affiliateService.createMyAffiliate(this.affiliate);
    
    operation.subscribe({
      next: () => {
        this.success = this.isEditing 
          ? '¡Perfil actualizado exitosamente!' 
          : '¡Perfil creado exitosamente!';
        setTimeout(() => this.router.navigate(['/credit/list']), 2000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.detail || 'Error al guardar el perfil';
      }
    });
  }
  
  validateForm(): boolean {
    if (!this.affiliate.documentType || !this.affiliate.documentNumber ||
        !this.affiliate.firstName || !this.affiliate.lastName ||
        !this.affiliate.email || !this.affiliate.birthDate) {
      this.error = 'Por favor complete todos los campos obligatorios';
      return false;
    }
    return true;
  }
  
  goBack(): void {
    this.router.navigate(['/credit/list']);
  }
}

