import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface AffiliateRequest {
  documentNumber: string;
  documentType: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  address?: string;
  birthDate: string;
}

export interface AffiliateResponse {
  id: number;
  documentNumber: string;
  documentType: string;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  phone?: string;
  address?: string;
  birthDate: string;
  userId?: number;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class AffiliateService {
  private apiUrl = `${environment.apiUrl}/affiliates`;
  
  constructor(private http: HttpClient) {}
  
  // Crear mi perfil de afiliado (vinculado al usuario autenticado)
  createMyAffiliate(affiliate: AffiliateRequest): Observable<AffiliateResponse> {
    return this.http.post<AffiliateResponse>(this.apiUrl, affiliate);
  }
  
  // Obtener mi perfil de afiliado
  getMyAffiliate(): Observable<AffiliateResponse | null> {
    return this.http.get<AffiliateResponse>(`${this.apiUrl}/me`).pipe(
      catchError(() => of(null))
    );
  }
  
  // Actualizar mi perfil de afiliado
  updateMyAffiliate(affiliate: AffiliateRequest): Observable<AffiliateResponse> {
    return this.http.put<AffiliateResponse>(`${this.apiUrl}/me`, affiliate);
  }
  
  // Para admins/analistas
  getAffiliateById(id: number): Observable<AffiliateResponse> {
    return this.http.get<AffiliateResponse>(`${this.apiUrl}/${id}`);
  }
}

