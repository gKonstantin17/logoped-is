import { Component, OnInit } from '@angular/core';

import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {UserData} from '../../utils/services/user-data.service';
import {KeycloakService} from '../../utils/oauth2/bff/keycloak.service';
import {UserDataStore} from '../../utils/stores/user-data.store';
import {PatientStore} from '../../utils/stores/patient.store';
import {LessonStore} from '../../utils/stores/lesson.store';
import {NotificationDto, WebSocketService} from '../../utils/websocket/WebSocketService';
import {Subscription} from 'rxjs';
import {NgForOf, NgIf, SlicePipe} from '@angular/common';


@Component({
  selector: 'app-private-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    NgIf,
    SlicePipe,
    NgForOf
  ],
  templateUrl: './private-layout.component.html',
  styleUrl: './private-layout.component.css'
})
export class PrivateLayoutComponent implements OnInit {
  userProfile?: UserData;
  notifications: NotificationDto[] = [];
  private wsSub: Subscription | null = null;

  constructor(private keycloakService: KeycloakService,

              private userDataStore: UserDataStore,
              private patientStore: PatientStore,
              private lessonStore:LessonStore,

              private websocketService: WebSocketService,
              private router:Router,) {}

  // после авторизации получение профиля, синхронизация с бд,
  // получение данных о пациентах и занятиях
  ngOnInit(): void {
    this.keycloakService.requestUserProfile().subscribe({
      next: (profile: any) => {
        this.userProfile = {
          id: profile.id,
          firstName: profile.given_name,
          lastName: profile.family_name,
          email: profile.email,
          phone: profile.phone,
          role: profile.role
        };

        console.log('User profile loaded', this.userProfile);
        this.userDataStore.setUserData(this.userProfile);

        this.keycloakService.isUserExist(this.userProfile).subscribe({
          next: exists => {
            // TODO логика когда найден или нет. Или зачем тогда?
            console.log('User exists:', exists);
          },
          error: err => {
            console.error('Error checking if user exists:', err);
          }
        });

        this.loadInitialData(this.userProfile.id, this.userProfile.role);

        this.websocketService.connect();
        this.websocketService.loadInitMessages(this.userProfile.id);

        this.wsSub = this.websocketService.messages$.subscribe({
          next: msgs => {
            console.log('Received notifications:', msgs);
            this.notifications = msgs;
          },
          error: err => console.error(err)
        });
      },
      error: err => {
        console.error('Error loading profile:', err);
      }
    });
  }
  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
  }
  private loadInitialData(userId: string, role: string) {
    this.patientStore.refresh(userId, role);
    this.lessonStore.refresh(userId, role);
  }


  logout(): void {
    this.keycloakService.logoutAction().subscribe({
      next: () => {
        // После успешного logout — делаем редирект
        window.location.href = ''; // или на нужную тебе страницу
      },
      error: (err) => {
        console.error('Logout failed', err);
        this.router.navigate(['']);
      }
    });
  }

  showNotifications = false;

  toggleNotifications() {
    this.showNotifications = !this.showNotifications;
  }
  closeNotifications() {
    this.showNotifications = false;
  }


}
