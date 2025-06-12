import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

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
  private userDataSubject = new BehaviorSubject<UserData | null>(null);
  userData$ = this.userDataSubject.asObservable();

  setUserData(data: UserData) {
    this.userDataSubject.next(data);
  }

  getUserData(): UserData | null {
    return this.userDataSubject.getValue();
  }
}

