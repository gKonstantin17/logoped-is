import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {HttpMethod} from '../oauth2/model/RequestBFF';
import {BackendService} from '../oauth2/backend/backend.service';


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
export class UserDataStore {
  private baseUrl = `${environment.RESOURSE_URL}/user`;

  constructor(private backend: BackendService) { }

  private userDataSubject = new BehaviorSubject<UserData | null>(null);
  userData$ = this.userDataSubject.asObservable();

  setUserData(data: UserData) {
    this.userDataSubject.next(data);
  }

  update(data: UserData): Observable<any> {
    return this.backend.createOperation(HttpMethod.PUT, `${this.baseUrl}/update/${data.id}`, data);
  }

  updateLogoped(data: UserData): Observable<any> {
    return this.backend.createOperation(HttpMethod.PUT, `${environment.RESOURSE_URL}/logoped/update/${data.id}`, data);
  }
}

