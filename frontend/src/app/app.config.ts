import {ApplicationConfig, importProvidersFrom, LOCALE_ID, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {adapterFactory} from 'angular-calendar/date-adapters/date-fns';
import {CalendarModule, DateAdapter} from 'angular-calendar';
import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {provideAnimations} from '@angular/platform-browser/animations';
import {SpinnerInterceptor} from './utils/oauth2/interceptor/spinner-inceptor.service';
import {CookiesInterceptorService} from './utils/oauth2/interceptor/cookies-interceptor.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    {provide: HTTP_INTERCEPTORS, useClass: CookiesInterceptorService, multi: true},// Регистрируем интерсептор
    {provide: HTTP_INTERCEPTORS, useClass: SpinnerInterceptor, multi: true},

    provideAnimations(),
    { provide: LOCALE_ID, useValue: 'ru' },
    importProvidersFrom(
      CalendarModule.forRoot({
        provide: DateAdapter,
        useFactory: adapterFactory,
      })
    )
  ]
};
