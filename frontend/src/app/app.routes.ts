import { Routes } from '@angular/router';
import {PublicLayoutComponent} from './layout/public-layout/public-layout.component';
import {PrivateLayoutComponent} from './layout/private-layout/private-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      { path: '', loadComponent: () => import('./public/home/home.component').then(m => m.HomeComponent) },
      { path: 'diagnostic', loadComponent: () => import('./public/diagnosticpromo/diagnosticpromo.component').then(m => m.DiagnosticPromoComponent)},
      { path: 'login', loadComponent: () => import('./public/login/login.component').then(m => m.LoginComponent)},
    ]
  },
  {
    path: 'dashboard',
    component: PrivateLayoutComponent,
    children: [
      { path: '', loadComponent: () => import('./dashboard/children/children.component').then(m => m.ChildrenComponent)},
      {path: 'children', loadComponent: () => import('./dashboard/children/children.component').then(m => m.ChildrenComponent)},
      {path: 'lessons', loadComponent: () => import('./dashboard/lessons/lessons.component').then(m => m.LessonsComponent)},
      {path: 'calendar', loadComponent: () => import('./dashboard/calendar/calendar.component').then(m => m.CalendarComponent)},
      {path: 'profile', loadComponent: () => import('./dashboard/profile/profile.component').then(m => m.ProfileComponent)},
      {path: 'details/:id', loadComponent: () => import('./dashboard/lessons/details/details.component').then(m => m.DetailsComponent)},
      {path: 'session', loadComponent: () => import('./dashboard/lessons/session/session.component').then(m => m.SessionComponent)},
      {path: 'diagnostic', loadComponent: () => import('./dashboard/lessons/diagnostic/diagnostic.component').then(m => m.DiagnosticComponent)},
      { path:'speechcard', loadComponent:() => import('./docs/speech-card/speech-card.component').then(m => m.SpeechCardComponent)},
    ]
  }
  ]
