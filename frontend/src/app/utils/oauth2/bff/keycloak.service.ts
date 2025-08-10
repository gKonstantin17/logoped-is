import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {UserData} from '../../services/user-data.service';
import {HttpMethod, Operation} from '../model/RequestBFF';
import {BackendService} from '../backend/backend.service';


@Injectable({
  providedIn: 'root'
})

export class KeycloakService {

  constructor(private http: HttpClient,
              private backendService: BackendService) {
  }


  // выход из системы
  logoutAction(): Observable<any> { //
    // просто вызываем адрес и ничего не возвращаем
    return this.http.get(environment.BFF_URI + '/bff/logout');
  }


  // получаем новые токены с помощью старого Refresh Token (из кука)
  exchangeRefreshToken(): Observable<any> {
    return this.http.get(environment.BFF_URI + '/bff/exchange');
  }

  // запрос данных пользователя (профайл)
  requestUserProfile()  {
    return this.http.get<UserData>(environment.BFF_URI + '/bff/profile');
  }
  isUserExist(userData: UserData): Observable<boolean> {
    const url = `${environment.RESOURSE_URL}/user/is-exist`;
    return this.backendService.createOperation(HttpMethod.POST, url, userData);
  }


}
