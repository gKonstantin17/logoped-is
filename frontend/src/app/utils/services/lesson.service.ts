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
export interface HomeworkDto {
  task: string;
}

// Интерфейс для урока (LessonChangeDto) под API
export interface LessonChangeDto {
  id: number;
  type: string; // на русском
  topic: string;
  description: string;
  patients: number[]; // теперь просто массив чисел
  homework: HomeworkDto | null;
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
    return this.backend.createOperation(HttpMethod.PUT, `${this.baseUrl}/change-date/${lessonId}`,newDate);
  }
  changeLesson(data:LessonChangeDto): Observable<any> {
    return this.backend.createOperation(HttpMethod.PUT, `${this.baseUrl}/change-lesson`,data);
  }
  checkTimeLesson(data: CheckAvailableTime): Observable<any> {
    return this.backend.createOperation(HttpMethod.POST,`${this.baseUrl}/check-time`,data);
  }
  updateStatus(data:LessonStatusDto): Observable<any> {
    return this.backend.createOperation(HttpMethod.PUT,`${this.baseUrl}/update-status`,data);
  }
}
