import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpMethod} from '../oauth2/model/RequestBFF';
import {Observable} from 'rxjs';
import {BackendService} from '../oauth2/backend/backend.service';

export interface SoundCorrection {
  id: number;
  sound: string;
  correction: string;
}

export interface SoundCorrectionChanges {
  added: SoundCorrection[];
  removed: SoundCorrection[];
}


@Injectable({
  providedIn: 'root'
})
export class SpeechCardService {
  private baseUrl = `${environment.RESOURSE_URL}`;

  constructor(private backend: BackendService) {}

  findAllError(): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/speecherror/findall`);
  }
  findById(speechCardId: number): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/speechcard/find-by-id`, speechCardId);
  }

  findByPatient(patientId: number): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/speechcard/find-by-patient`, patientId);
  }

  findFirstAllByPatient(logopedId:string): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/speechcard/find-firsts-by-logoped`, logopedId);
  }
  findCorrectionsByPatient(patientId: number): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/soundcorrection/find-by-patient`, patientId);
  }
  findPatientHistory(patientId: number): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/speechcard/find-patient-history`, patientId);
  }
  createWithDiagnostic(data: any): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/speechcard/create-with-diagnostic`, data);
  }
  updateCorrections(data:any): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/speechcard/create-with-corrections`, data);
  }
  findChangedCorrections(data:any): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/soundcorrection/find-changes`, data);
  }
}
