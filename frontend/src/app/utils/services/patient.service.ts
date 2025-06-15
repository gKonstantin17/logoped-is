import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {HttpMethod, Operation} from '../oauth2/model/RequestBFF';
import {Observable} from 'rxjs';

export interface PatientData {
  firstName: string,
  lastName: string,
  dateOfBirth: string
}
export interface PatientChangeData {
  firstName: string,
  lastName: string,
  dateOfBirth: string
}
@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private baseUrl = `${environment.RESOURSE_URL}/patient`;
  private bffUrl = `${environment.BFF_URI}/bff/operation`;

  constructor(private http: HttpClient) { }

  private createOperation(method: HttpMethod, url: string, body?: any): Observable<any> {
    const operation = new Operation(method, url, body ? JSON.stringify(body) : null);
    return this.http.post<any>(this.bffUrl, JSON.stringify(operation));
  }

  findByUser(userId: string): Observable<any[]> {
    return this.createOperation(HttpMethod.POST, `${this.baseUrl}/find-by-user`, userId);
  }

  findByLogoped(logopedId: string): Observable<any[]> {
    return this.createOperation(HttpMethod.POST, `${this.baseUrl}/find-by-logoped`, logopedId);
  }

  create(data: PatientData): Observable<any> {
    return this.createOperation(HttpMethod.POST, `${this.baseUrl}/create`, data);
  }

  update(data: PatientChangeData, id: number): Observable<any> {
    return this.createOperation(HttpMethod.PUT, `${this.baseUrl}/update/${id}`, data);
  }

  delete(patientId: number): Observable<any> {
    return this.createOperation(HttpMethod.DELETE, `${this.baseUrl}/delete/${patientId}`);
  }

  existsSpeechCard(patientId: number): Observable<any> {
    return this.createOperation(HttpMethod.POST, `${this.baseUrl}/exists-speechcard`, patientId);
  }
}
