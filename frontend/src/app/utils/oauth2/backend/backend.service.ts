// backend.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {HttpMethod, Operation} from '../model/RequestBFF';
import {environment} from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BackendService {
  private bffUrl = `${environment.BFF_URI}/bff/operation`;

  constructor(private http: HttpClient) {}

  createOperation(method: HttpMethod, url: string, body?: any): Observable<any> {
    const operation = new Operation(method, url, body ? JSON.stringify(body) : null);
    return this.http.post<any>(this.bffUrl, JSON.stringify(operation));
  }
}
