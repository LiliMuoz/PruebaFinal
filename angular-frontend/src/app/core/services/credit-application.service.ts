import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface CreditApplicationRequest {
  requestedAmount: number;
  termMonths: number;
  purpose?: string;
}

export interface RiskEvaluation {
  id: number;
  score: number;
  riskLevel: string;
  recommendation: string;
  evaluatedAt: string;
}

export interface CreditApplicationResponse {
  id: number;
  affiliateId: number;
  affiliateName: string;
  requestedAmount: number;
  termMonths: number;
  interestRate: number;
  monthlyPayment: number;
  purpose?: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';
  createdAt: string;
  evaluatedAt?: string;
  evaluatedBy?: string;
  rejectionReason?: string;
  riskEvaluation?: RiskEvaluation;
}

@Injectable({
  providedIn: 'root'
})
export class CreditApplicationService {
  private apiUrl = `${environment.apiUrl}/credit-applications`;
  
  constructor(private http: HttpClient) {}
  
  createApplication(application: CreditApplicationRequest): Observable<CreditApplicationResponse> {
    return this.http.post<CreditApplicationResponse>(this.apiUrl, application);
  }
  
  getApplicationById(id: number): Observable<CreditApplicationResponse> {
    return this.http.get<CreditApplicationResponse>(`${this.apiUrl}/${id}`);
  }
  
  // Obtener mis solicitudes (para afiliados)
  getMyApplications(): Observable<CreditApplicationResponse[]> {
    return this.http.get<CreditApplicationResponse[]>(`${this.apiUrl}/me`);
  }
  
  // Obtener todas las solicitudes (para analistas/admins)
  getAllApplications(): Observable<CreditApplicationResponse[]> {
    return this.http.get<CreditApplicationResponse[]>(this.apiUrl);
  }
  
  evaluateApplication(id: number): Observable<CreditApplicationResponse> {
    return this.http.post<CreditApplicationResponse>(`${this.apiUrl}/${id}/evaluate`, {});
  }
  
  approveApplication(id: number): Observable<CreditApplicationResponse> {
    return this.http.post<CreditApplicationResponse>(`${this.apiUrl}/${id}/approve`, {});
  }
  
  rejectApplication(id: number, reason: string): Observable<CreditApplicationResponse> {
    return this.http.post<CreditApplicationResponse>(`${this.apiUrl}/${id}/reject`, { reason });
  }
  
  cancelApplication(id: number): Observable<CreditApplicationResponse> {
    return this.http.post<CreditApplicationResponse>(`${this.apiUrl}/${id}/cancel`, {});
  }
}

