// notifications.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { WebSocketService, NotificationDto } from '../../utils/websocket/WebSocketService';
import {combineLatest, Subscription} from 'rxjs';
import { PatientStore } from '../../utils/stores/patient.store';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {RouterLink} from '@angular/router';
import {LessonStore} from '../../utils/stores/lesson.store';
import {UserDataStore} from '../../utils/stores/user-data.store';

@Component({
  selector: 'app-notifications',
  standalone: true,
  templateUrl: './notifications.component.html',
  imports: [
    NgForOf,
    RouterLink,
    NgIf,
    DatePipe
  ],
  styleUrl: './notifications.component.css'
})
export class NotificationsComponent implements OnInit, OnDestroy {
  // Сырые уведомления
  private notifications: NotificationDto[] = [];

  // Данные для отображения
  notificationsWithDetails: {
    id: number,
    sendDate: string,
    patients: { id: number; name: string }[],
    message: string,
    lessonTopic: string,
    lessonId: number,
    received: boolean
  }[] = [];

  private wsSub: Subscription | null = null;

  constructor(
    private userDataStore: UserDataStore,
    private websocketService: WebSocketService,
    private lessonStore: LessonStore,
    private patientStore: PatientStore
  ) {}
  userId: string | null = null;
  ngOnInit(): void {
    this.userDataStore.userData$.subscribe(user => {
      this.userId = user?.id || null;
      });
    // Берём актуальные массивы пациентов и уроков
    const currentPatients = this.patientStore['patientsSubject'].getValue();
    const currentLessons = this.lessonStore['lessonsSubject'].getValue();

    const patientMap = new Map<number, string>();
    currentPatients.forEach(p => patientMap.set(+p.id, `${p.firstName} ${p.lastName}`));

    const lessonMap = new Map<number, any>();
    currentLessons.forEach(l => lessonMap.set(+l.id, l));

    // Загружаем уведомления через HTTP
    this.websocketService.loadMessages(this.userId!).subscribe({
      next: (notifications: NotificationDto[]) => {
        this.notifications = notifications;

        this.notificationsWithDetails = notifications.map(n => {
          const lesson = lessonMap.get(+n.lessonNoteId);
          return {
            id: n.id,
            sendDate: n.sendDate,
            patients: (n.patientsId ?? []).map(id => ({ id: +id, name: patientMap.get(+id) || 'Неизвестный пациент' })),
            message: n.message,
            lessonTopic: lesson?.topic ?? 'Без темы',
            lessonId: lesson?.id ?? +n.lessonNoteId,
            received: n.received
          };
        }).sort((a, b) => new Date(b.sendDate).getTime() - new Date(a.sendDate).getTime());
      },
      error: err => console.error('Ошибка при загрузке уведомлений через HTTP:', err)
    });
  }





  markAsRead(notification: { id: number; received: boolean }, event?: MouseEvent) {
    event?.stopPropagation();
    event?.preventDefault();

    if (!notification.received) {
      // Сразу помечаем локально, чтобы UI обновился
      notification.received = true;

      // Отправляем на сервер асинхронно
      this.websocketService.markAsReceived(notification.id);
    }
  }

  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
  }
}
