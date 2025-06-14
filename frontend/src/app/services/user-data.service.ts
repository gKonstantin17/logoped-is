import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';

export interface UserData {
  id: number;
  firstName: string;
  secondName: string;
  email: string;
  phone: string;
  role: string;
}

@Injectable({
  providedIn: 'root' // сервис будет синглтоном на всё приложение
})
export class UserDataService {
  private apiUrl = `${environment.RESOURSE_URL}/user`;

  constructor(private http: HttpClient) { }

  private userDataSubject = new BehaviorSubject<UserData | null>(null);
  userData$ = this.userDataSubject.asObservable();

  setUserData(data: UserData) {
    this.userDataSubject.next(data);
  }

  getUserData(): UserData | null {
    return this.userDataSubject.getValue();
  }

  update(data:UserData) {
    return this.http.put<any>(`${this.apiUrl}/update/${data.id}`,data);
  }
  updateLogoped(data:UserData) {
    return this.http.put<any>(`${environment.RESOURSE_URL}/logoped/update/${data.id}`,data);
  }
}

