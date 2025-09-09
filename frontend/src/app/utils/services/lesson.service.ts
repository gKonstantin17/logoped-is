import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpMethod, Operation} from '../oauth2/model/RequestBFF';
import {Observable} from 'rxjs';
import {BackendService} from '../oauth2/backend/backend.service';
import {LessonStatus} from '../enums/lesson-status.enum';
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
  status:LessonStatus;
  logoped: {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
  } | null;
  homework: {task:string|null} | null;
  patients: {
    id: number;
    firstName: string;
    lastName: string;
    dateOfBirth: string;
  }[];
}
export interface CheckAvailableTime {
  patientId: number;
  date: string;
}
export interface LessonStatusDto {
  id:number;
  status:string;
}
@Injectable({
  providedIn: 'root'
})
export class LessonService {
  private baseUrl = `${environment.RESOURSE_URL}/lesson`;

  constructor(private backend: BackendService) {}

  findWithFk(id: number): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/find-with-fk`, id);
  }

  findByUser(userId: string): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/find-by-user`, userId);
  }

  findByLogoped(logopedId: string): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/find-by-logoped`, logopedId);
  }

  createLesson(data: LessonData): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/create`, data);
  }

  cancelLesson(lessonId:number): Observable<any> {
    return this.backend.createOperation(HttpMethod.PUT, `${this.baseUrl}/cancel/${lessonId}`,);
  }
  changeDateLesson(lessonId:number, newDate:Date): Observable<any> {
    return this.backend.createOperation(HttpMethod.PUT, `${this.baseUrl}/change-date-by-patient/${lessonId}`,newDate);
  }
  // checkTimeLesson(patientId: number, date: Date): Observable<any> {
  //   const body = { date: date.toISOString() };
  //   return this.backend.createOperation(HttpMethod.POST,`${this.baseUrl}/check-time/${patientId}`,body);
  // }
  checkTimeLesson(data: CheckAvailableTime): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST,`${this.baseUrl}/check-time`,data);
  }
  updateStatus(data:LessonStatusDto): Observable<any> {
    return this.backend.createOperation(HttpMethod.PUT,`${this.baseUrl}/update-status`,data);
  }
}
