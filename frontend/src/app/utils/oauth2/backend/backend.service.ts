import {Inject, Injectable, InjectionToken} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {HttpMethod, Operation} from '../../oauth2/model/RequestBFF';

@Injectable({
  providedIn: 'root'
})

export class BackendService {
  constructor(private http: HttpClient) {}


  // запрос в BFF
  requestTestData(): Observable<any> { // Observable - чтобы подписываться на результат выполнения запроса через subscribe
    const url = environment.RESOURCE_URI + '/data/test'; // это адрес, который BFF будет вызывать у Resource Server, добавляя к запросу access token
    const httpMethod = HttpMethod.GET; // тип запроса тоже важно указывать
    const body = null;
    const operation = new Operation(httpMethod,url,body); // данные для BFF операции (уточняем для BFF какой именно запрос он должен выполнить в RS)

    const req_body = JSON.stringify(operation);
    return this.http.post(environment.BFF_URI + '/bff/operation', req_body);
  }


}
