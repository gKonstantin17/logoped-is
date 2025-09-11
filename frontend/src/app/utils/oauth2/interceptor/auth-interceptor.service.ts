import {Injectable} from "@angular/core";
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable, switchMap, throwError} from "rxjs";
import {KeycloakService} from '../bff/keycloak.service';
import {Router} from '@angular/router';
import {catchError} from 'rxjs/operators';

@Injectable()
export class AuthInterceptorService implements HttpInterceptor {
  private isRefreshing = false;

  constructor(private keycloakService: KeycloakService, private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        console.log('Intercepted error:', error.status);

        // Предотвращаем повторную попытку обновления токена
        //if ((error.status === 401) && !this.isRefreshing) {
        if ((error.status === 401 || error.status === 403) && !this.isRefreshing) {
          this.isRefreshing = true;
          console.log('Attempting to refresh token...');

          return this.keycloakService.exchangeRefreshToken().pipe(
            switchMap(() => {
              console.log('Token refreshed. Retrying request...');
              this.isRefreshing = false;
              return next.handle(req);
            }),
            catchError(err => {
              this.isRefreshing = false;
              console.error('Token refresh failed:', err);
              this.router.navigate(['/login']);
              return throwError(() => err);
            })
          );
        }

        // Если уже пробовали обновить — не зацикливаем
        if (this.isRefreshing) {
          console.warn('Already attempted token refresh. Forcing logout.');
          this.router.navigate(['/login']);
        }

        return throwError(() => error);
      })
    );
  }
}

