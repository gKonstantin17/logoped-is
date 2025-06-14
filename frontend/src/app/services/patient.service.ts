import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';

export interface PatientData {
  firstName: string,
  secondName: string,
  dateOfBirth: string
}
export interface PatientChangeData {
  firstName: string,
  secondName: string,
  dateOfBirth: string
}
@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private apiUrl = `${environment.RESOURSE_URL}/patient`;

  constructor(private http: HttpClient) { }
  findByUser(userId: number) {
    return this.http.post<any[]>(`${this.apiUrl}/find-by-user`, userId);
  }
  findByLogoped(logopedId: number) {
    return this.http.post<any[]>(`${this.apiUrl}/find-by-logoped`, logopedId);
  }
  create(data:PatientData) {
    return this.http.post<any>(`${this.apiUrl}/create`,data);
  }

  update(data:PatientChangeData, id:number) {
    return this.http.put<any>(`${this.apiUrl}/update/${id}`,data);
  }

  delete(patientId:number) {
    return this.http.delete<any>(`${this.apiUrl}/delete/${patientId}`);
  }

  existsSpeechCard(patientId:number) {
    return this.http.post<any>(`${this.apiUrl}/exists-speechcard`,patientId);
  }
}
