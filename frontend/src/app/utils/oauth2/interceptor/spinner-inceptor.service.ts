import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from '@angular/common/http';
import {SpinnerService} from '../spinner/spinner.service';
import {Observable, tap} from 'rxjs';

@Injectable()
export class SpinnerInterceptor implements HttpInterceptor {

  constructor(
    private spinnerService: SpinnerService // переключатель вкл/выкл для спиннера
  ) {
  }
  // сам запрос не трогаем, но на отслеживаем выполнение (handle)
  // pipe-tap

  // метод вызывается автоматически для каждого исходящего запроса
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    this.spinnerService.show(); // вначале показываем спиннер

    // https://rxjs.dev/api/index/function/tap
    // https://rxjs.dev/api/index/function/pipe

    // реакция на выполнение каждого запроса: показ и скрытие индикатора загрузки
    return next.handle(req)
      .pipe(
        tap({

          // успешное выполнение
          next:
            (event: HttpEvent<any>) => {
              if (event instanceof HttpResponse) { // пришел ответ - значит запрос завершен
                this.spinnerService.hide(); // когда запрос выполнился - скрыть спиннер
              }
            },

          // ошибка выполнения
          error: (error) => {
            console.log(error);
            this.spinnerService.hide(); // если возникла ошибка - скрыть спиннер
          }

        })
      );
  }
}
