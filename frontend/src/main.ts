import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import {registerLocaleData} from '@angular/common';
import localeRu from '@angular/common/locales/ru';

// ng serve --host 0.0.0.0
//npm install angular-calendar date-fns
//npm install @angular/animations

registerLocaleData(localeRu);
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
