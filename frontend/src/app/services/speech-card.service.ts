import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SpeechCardService {
  private apiUrl = `${environment.RESOURSE_URL}`;

  constructor(private http: HttpClient) { }

  findAllError () {
    return this.http.post<any>(`${this.apiUrl}/speecherror/findall`,null);
  }
  findByPatient(patientId: number) {
    return this.http.post<any>(`${this.apiUrl}/speechcard/find-by-patient`,patientId);
  }
  createWithDiagnostic(data: any) {
    return this.http.post(`${this.apiUrl}/speechcard/create-with-diagnostic`, data);
  }

}
