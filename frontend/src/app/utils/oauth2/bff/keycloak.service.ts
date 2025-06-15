import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../model/User';
import {environment} from '../../../../environments/environment';
import {UserData} from '../../services/user-data.service';
import {HttpMethod, Operation} from '../model/RequestBFF';


@Injectable({
  providedIn: 'root'
})

export class KeycloakService {

  constructor(private http: HttpClient) {
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
  isUserExist(userData: UserData): Observable<any> {
    const url = 'http://localhost:8280/user/is-exist';
    const httpMethod = HttpMethod.POST;
    const body = JSON.stringify(userData);
    const operation = new Operation(httpMethod, url, body);
    return this.http.post<boolean>(environment.BFF_URI + '/bff/operation', JSON.stringify(operation));
  }


}
