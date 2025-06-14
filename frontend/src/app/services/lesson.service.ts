import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
export interface LessonData {
  type: string,
  topic: string,
  description: string,
  dateOfLesson: string,
  logopedId: number | null,
  homework: string | null,
  patientsId: number[]
}
@Injectable({
  providedIn: 'root'
})
export class LessonService {
  private apiUrl = `${environment.RESOURSE_URL}/lesson`;

  constructor(private http: HttpClient) { }

  findWithFk(id:number) {
    return this.http.post<any>(`${this.apiUrl}/find-with-fk`,id);
  }
  findByUser(userId:number) {
    return this.http.post<any>(`${this.apiUrl}/find-by-user`,userId);
  }
  findByLogoped(logopedId:number) {
    return this.http.post<any>(`${this.apiUrl}/find-by-logoped`,logopedId);
  }
  createLesson(data: LessonData) {
    return this.http.post<any>(`${this.apiUrl}/create`, data);
  }

}
