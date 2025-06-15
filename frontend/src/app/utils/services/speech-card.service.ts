import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {HttpMethod, Operation} from '../oauth2/model/RequestBFF';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SpeechCardService {
  private baseUrl = `${environment.RESOURSE_URL}`;
  private bffUrl = `${environment.BFF_URI}/bff/operation`;

  constructor(private http: HttpClient) {}

  private createOperation(method: HttpMethod, url: string, body?: any): Observable<any> {
    const operation = new Operation(method, url, body ? JSON.stringify(body) : null);
    return this.http.post<any>(this.bffUrl, JSON.stringify(operation));
  }

  findAllError(): Observable<any> {
    return this.createOperation(HttpMethod.POST, `${this.baseUrl}/speecherror/findall`);
  }

  findByPatient(patientId: number): Observable<any> {
    return this.createOperation(HttpMethod.POST, `${this.baseUrl}/speechcard/find-by-patient`, patientId);
  }

  createWithDiagnostic(data: any): Observable<any> {
    return this.createOperation(HttpMethod.POST, `${this.baseUrl}/speechcard/create-with-diagnostic`, data);
  }
}
