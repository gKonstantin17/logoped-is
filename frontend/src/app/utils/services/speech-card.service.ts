import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpMethod} from '../oauth2/model/RequestBFF';
import {Observable} from 'rxjs';
import {BackendService} from '../oauth2/backend/backend.service';

@Injectable({
  providedIn: 'root'
})
export class SpeechCardService {
  private baseUrl = `${environment.RESOURSE_URL}`;

  constructor(private backend: BackendService) {}

  findAllError(): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/speecherror/findall`);
  }

  findByPatient(patientId: number): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/speechcard/find-by-patient`, patientId);
  }
  findCorrectionsByPatient(patientId: number): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/soundcorrection/find-by-patient`, patientId);
  }
  createWithDiagnostic(data: any): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/speechcard/create-with-diagnostic`, data);
  }
  updateCorrections(data:any): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/speechcard/create-with-corrections`, data);
  }
}
