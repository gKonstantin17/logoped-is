import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {HttpMethod, Operation} from '../oauth2/model/RequestBFF';
import {LessonService} from './lesson.service';
import {PatientService} from './patient.service';

export interface UserData {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  role: string;
}

@Injectable({
  providedIn: 'root' // сервис будет синглтоном на всё приложение
})
export class UserDataService {
  private baseUrl = `${environment.RESOURSE_URL}/user`;
  private bffUrl = `${environment.BFF_URI}/bff/operation`;

  constructor(private http: HttpClient,
              private lessonService:LessonService,
              private patientService:PatientService) { }

  private userDataSubject = new BehaviorSubject<UserData | null>(null);
  userData$ = this.userDataSubject.asObservable();

  private patientsSubject = new BehaviorSubject<any[]>([]);
  patients$ = this.patientsSubject.asObservable();

  private lessonsSubject = new BehaviorSubject<any[]>([]);
  lessons$ = this.lessonsSubject.asObservable();

  setUserData(data: UserData) {
    this.userDataSubject.next(data);
  }

  setPatients(patients: any[]) {
    this.patientsSubject.next(patients);
  }

  setLessons(lessons: any[]) {
    this.lessonsSubject.next(lessons);
  }

  refreshLessons(userId: string, role: string): void {
    const obs = role === 'logoped' ?
      this.lessonService.findByLogoped(userId) :
      this.lessonService.findByUser(userId);

    obs.subscribe({
      next: lessons => this.setLessons(lessons),
      error: err => console.error('Ошибка при обновлении занятий:', err)
    });
  }
  refreshPatients(userId: string, role: string): void {
    const obs = role === 'logoped' ?
      this.patientService.findByLogoped(userId) :
      this.patientService.findByUser(userId);

    obs.subscribe({
      next: patients => this.setPatients(patients),
      error: err => console.error('Ошибка при обновлении занятий:', err)
    });
  }
  private createOperation(method: HttpMethod, url: string, body?: any): Observable<any> {
    const operation = new Operation(method, url, body ? JSON.stringify(body) : null);
    return this.http.post<any>(this.bffUrl, JSON.stringify(operation));
  }

  update(data: UserData): Observable<any> {
    return this.createOperation(HttpMethod.PUT, `${this.baseUrl}/update/${data.id}`, data);
  }

  updateLogoped(data: UserData): Observable<any> {
    return this.createOperation(HttpMethod.PUT, `${environment.RESOURSE_URL}/logoped/update/${data.id}`, data);
  }
}

