import {Injectable} from "@angular/core";
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable, switchMap, throwError} from "rxjs";
import {KeycloakService} from '../bff/keycloak.service';
import {Router} from '@angular/router';
import {catchError} from 'rxjs/operators';

@Injectable()
export class AuthInterceptorService implements HttpInterceptor {
  constructor(private keycloakService: KeycloakService, private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Попробовать обновить токен
          return this.keycloakService.exchangeRefreshToken().pipe(
            switchMap(() => {
              // Повторяем исходный запрос
              return next.handle(req);
            }),
            catchError(err => {
              // Если обновление не удалось — отправляем на логин
              this.router.navigate(['/login']);
              return throwError(() => err);
            })
          );
        } else {
          return throwError(() => error);
        }
      })
    );
  }
}
