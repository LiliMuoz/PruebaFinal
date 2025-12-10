import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { 
    path: 'login', 
    loadComponent: () => import('./modules/auth/components/login.component').then(m => m.LoginComponent)
  },
  { 
    path: 'register', 
    loadComponent: () => import('./modules/auth/components/register.component').then(m => m.RegisterComponent)
  },
  { 
    path: 'affiliate/new', 
    loadComponent: () => import('./modules/affiliate/components/affiliate-form.component').then(m => m.AffiliateFormComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'credit/new', 
    loadComponent: () => import('./modules/credit-application/components/credit-application-form.component').then(m => m.CreditApplicationFormComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'credit/list', 
    loadComponent: () => import('./modules/credit-application/components/credit-list.component').then(m => m.CreditListComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'admin', 
    loadComponent: () => import('./modules/admin/components/admin-panel.component').then(m => m.AdminPanelComponent),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: '/login' }
];

