import 'zone.js/testing';
import { getTestBed } from '@angular/core/testing';
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting
} from '@angular/platform-browser-dynamic/testing';

// Инициализация среды тестирования Angular
getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting()
);

// Подключение всех файлов *.spec.ts
const tests = (require as any).context('./', true, /\.spec\.ts$/);
tests.keys().forEach(tests);
