import {Injectable} from "@angular/core";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable()
export class CookiesInterceptorService implements HttpInterceptor {


  intercept(req: HttpRequest<any>, handler: HttpHandler): Observable<HttpEvent<unknown>> {
    req = req.clone({

      // заголовки, чтобы BFF корректно отработал запрос
      setHeaders: {
        'Content-Type': 'application/json; charset=utf-8',
        'Access-Control-Allow-Headers': '*',
        'Access-Control-Allow-Methods': '*',
        'Access-Control-Allow-Origin': '*'
      },
      // чтобы браузер прикреплял куки (в которых хранятся токены)
      withCredentials: true,
    });
    return handler.handle(req);
  }
}
