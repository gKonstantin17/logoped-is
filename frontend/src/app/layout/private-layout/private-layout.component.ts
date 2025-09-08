import {Component, HostListener, OnInit} from '@angular/core';

import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {UserData} from '../../utils/services/user-data.service';
import {KeycloakService} from '../../utils/oauth2/bff/keycloak.service';
import {UserDataStore} from '../../utils/stores/user-data.store';
import {PatientStore} from '../../utils/stores/patient.store';
import {LessonStore} from '../../utils/stores/lesson.store';
import {NotificationDto, WebSocketService} from '../../utils/websocket/WebSocketService';
import {combineLatest, Subscription} from 'rxjs';
import {DatePipe, NgClass, NgForOf, NgIf, SlicePipe} from '@angular/common';


@Component({
  selector: 'app-private-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    NgIf,
    SlicePipe,
    NgForOf,
    NgClass,
    DatePipe
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
  notificationsWithNames: { message: string, patientNames: string[], received:boolean, sendDate:string }[] = [];

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
        this.userDataStore.setUserData(this.userProfile);

        this.loadInitialData(this.userProfile.id, this.userProfile.role);

        this.websocketService.connect();
        this.websocketService.loadInitMessages(this.userProfile.id);

        // Подписка на уведомления и пациентов
        this.wsSub = combineLatest([
          this.websocketService.messages$,
          this.patientStore.patients$
        ]).subscribe(([notifications, patients]) => {
          // Сохраняем «сырые» уведомления для unreadCount
          this.notifications = notifications;

          // Создаём Map для быстрого поиска пациентов
          const patientMap = new Map<number, string>();
          patients.forEach(p => patientMap.set(p.id, `${p.firstName} ${p.lastName}`));

          // Пересчитываем массив для отображения, сортируем по дате
          this.notificationsWithNames = notifications
            .map(n => ({
              message: n.message,
              patientNames: (n.patientsId ?? []).map((id:any) => patientMap.get(id) || 'Неизвестный пациент'),
              received: n.received,
              sendDate: n.sendDate
            }))
            .sort((a, b) => {
              // сортировка по дате: последние сверху
              const nA = new Date(notifications.find(n1 => n1.message === a.message)?.sendDate ?? 0).getTime();
              const nB = new Date(notifications.find(n1 => n1.message === b.message)?.sendDate ?? 0).getTime();
              return nB - nA;
            });
        });
      },
      error: err => console.error('Error loading profile:', err)
    });
  }



  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
  }
  private loadInitialData(userId: string, role: string) {
    this.patientStore.refresh(userId, role);
    this.lessonStore.refresh(userId, role);
  }
  get unreadCount(): number {
    return this.notifications.filter(n => !n.received).length;
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

  // Закрываем окно при клике вне него
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    const notificationsPopup = document.querySelector('.notifications-popup');

    // Если клик не внутри окна уведомлений и не по иконке сообщения
    if (
      this.showNotifications &&
      notificationsPopup &&
      !notificationsPopup.contains(target) &&
      !target.closest('.icon-container')
    ) {
      this.closeNotifications();
    }
  }


}
