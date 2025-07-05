import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, tap, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';
import {UserDataService} from '../services/user-data.service';


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

  constructor(private userDataService: UserDataService) { }

  private userDataSubject = new BehaviorSubject<UserData | null>(null);
  userData$ = this.userDataSubject.asObservable();

  setUserData(data: UserData) {
    this.userDataSubject.next(data);
  }

  update(data: UserData): Observable<any> {
    let update$;

    if (data.role === 'user') {
      update$ = this.userDataService.update(data);
    } else if (data.role === 'logoped') {
      update$ = this.userDataService.updateLogoped(data);
    } else {
      return throwError(() => new Error('Unknown user role'));
    }

    return update$.pipe(
      tap(() => {
        this.setUserData(data); // Обновляем состояние после успешного запроса
      })
    );
  }
}

