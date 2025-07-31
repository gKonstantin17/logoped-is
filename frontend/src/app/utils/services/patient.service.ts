import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpMethod} from '../oauth2/model/RequestBFF';
import {Observable} from 'rxjs';
import {BackendService} from '../oauth2/backend/backend.service';

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

  constructor(private backend: BackendService) { }

  findByUser(userId: string): Observable<any[]> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/find-by-user`, userId);
  }

  findByLogoped(logopedId: string): Observable<any[]> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/find-by-logoped`, logopedId);
  }

  findByLogopedWithSC(logopedId: string): Observable<any[]> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/findall-with-sc`, logopedId);
  }

  create(data: PatientData): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/create`, data);
  }

  update(data: PatientChangeData, id: number): Observable<any> {
    return this.backend.createOperation(HttpMethod.PUT, `${this.baseUrl}/update/${id}`, data);
  }

  hide(patientId: number): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/hide/${patientId}`,patientId);
  }
  restore(patientId: number): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/restore/${patientId}`,patientId);
  }
  existsSpeechCard(patientId: number): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/exists-speechcard`, patientId);
  }
}
