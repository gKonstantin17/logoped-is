import { Routes } from '@angular/router';
import {PublicLayoutComponent} from './layout/public-layout/public-layout.component';
import {PrivateLayoutComponent} from './layout/private-layout/private-layout.component';

export const routes: Routes = [
  {
    // веб-страницы до авторизации (промо страницы)
    path: '',
    component: PublicLayoutComponent,
    children: [
      // домашняя
      { path: '', loadComponent: () => import('./public/home/home.component').then(m => m.HomeComponent) },
      // о диагностике
      { path: 'diagnostic', loadComponent: () => import('./public/diagnosticpromo/diagnosticpromo.component').then(m => m.DiagnosticPromoComponent)},
      // страница авторизации
      { path: 'login', loadComponent: () => import('./utils/oauth2/login/login.component').then(m => m.LoginComponent)},
    ]
  },
  {
    // веб-страницы после авторизации (с основным функционалом)
    path: 'dashboard',
    component: PrivateLayoutComponent,
    children: [
      // пациенты (дети)
      { path: '', loadComponent: () => import('./dashboard/children/children.component').then(m => m.ChildrenComponent)},
      {path: 'children', loadComponent: () => import('./dashboard/children/children.component').then(m => m.ChildrenComponent)},
      // занятия
      {path: 'lessons', loadComponent: () => import('./dashboard/lessons/lessons.component').then(m => m.LessonsComponent)},
      // расписание
      {path: 'calendar', loadComponent: () => import('./dashboard/calendar/calendar.component').then(m => m.CalendarComponent)},
      // учетная запись
      {path: 'profile', loadComponent: () => import('./dashboard/profile/profile.component').then(m => m.ProfileComponent)},
      // детали занятия
      {path: 'details/:id', loadComponent: () => import('./dashboard/lessons/details/details.component').then(m => m.DetailsComponent)},
      // проведение занятия
      {path: 'session', loadComponent: () => import('./dashboard/lessons/session/session.component').then(m => m.SessionComponent)},
      // проведение диагностики
      {path: 'diagnostic', loadComponent: () => import('./dashboard/lessons/diagnostic/diagnostic.component').then(m => m.DiagnosticComponent)},
      // сформированная речевая карта
      { path:'speechcard', loadComponent:() => import('./docs/speech-card/speech-card.component').then(m => m.SpeechCardComponent)},
      { path:'notifications', loadComponent:() => import('./dashboard/notifications/notifications.component').then(m => m.NotificationsComponent)},
    ]
  }
]
