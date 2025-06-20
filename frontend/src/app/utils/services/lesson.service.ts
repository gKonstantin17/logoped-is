import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {HttpMethod, Operation} from '../oauth2/model/RequestBFF';
import {Observable} from 'rxjs';
export interface LessonData {
  type: string,
  topic: string,
  description: string,
  dateOfLesson: string,
  logopedId: string | null,
  homework: string | null,
  patientsId: number[]
}
export interface LessonFullData {
  id: number;
  type: string;
  topic: string;
  description: string;
  dateOfLesson: string;
  logoped: {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
  } | null;
  homework: string | null;
  patients: {
    id: number;
    firstName: string;
    lastName: string;
    dateOfBirth: string;
  }[];
}

@Injectable({
  providedIn: 'root'
})
export class LessonService {
  private baseUrl = `${environment.RESOURSE_URL}/lesson`;
  private bffUrl = `${environment.BFF_URI}/bff/operation`;

  constructor(private http: HttpClient) {}

  private createOperation(method: HttpMethod, url: string, body?: any): Observable<any> {
    const operation = new Operation(method, url, body ? JSON.stringify(body) : null);
    return this.http.post<any>(this.bffUrl, JSON.stringify(operation));
  }

  findWithFk(id: number): Observable<any> {
    return this.createOperation(HttpMethod.POST, `${this.baseUrl}/find-with-fk`, id);
  }

  findByUser(userId: string): Observable<any> {
    return this.createOperation(HttpMethod.POST, `${this.baseUrl}/find-by-user`, userId);
  }

  findByLogoped(logopedId: string): Observable<any> {
    return this.createOperation(HttpMethod.POST, `${this.baseUrl}/find-by-logoped`, logopedId);
  }

  createLesson(data: LessonData): Observable<any> {
    return this.createOperation(HttpMethod.POST, `${this.baseUrl}/create`, data);
  }
}
