import { Routes } from '@angular/router';
import {PublicLayoutComponent} from './layout/public-layout/public-layout.component';
import {PrivateLayoutComponent} from './layout/private-layout/private-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      { path: '', loadComponent: () => import('./public/home/home.component').then(m => m.HomeComponent) },
      { path: 'diagnostic', loadComponent: () => import('./public/diagnostic/diagnostic.component').then(m => m.DiagnosticComponent)},
      { path: 'login', loadComponent: () => import('./public/login/login.component').then(m => m.LoginComponent)},
    ]
  },
  {
    path: 'dashboard',
    component: PrivateLayoutComponent,
    children: [
      {path: 'children', loadComponent: () => import('./dashboard/children/children.component').then(m => m.ChildrenComponent)},
      {path: 'lessons', loadComponent: () => import('./dashboard/lessons/lessons.component').then(m => m.LessonsComponent)}
    ]
  }
  ]
