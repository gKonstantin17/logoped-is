// notifications.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { WebSocketService, NotificationDto } from '../../utils/websocket/WebSocketService';
import { Subscription } from 'rxjs';
import { PatientStore } from '../../utils/stores/patient.store';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {RouterLink} from '@angular/router';
import {LessonStore} from '../../utils/stores/lesson.store';

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
  notificationsWithDetails: { sendDate: string, patients: { id: number, name: string }[], message: string, lessonTopic: string, lessonId: number }[] = [];
  private wsSub: Subscription | null = null;
  private lessonsSub: Subscription | null = null;
  private patientsSub: Subscription | null = null;

  private notifications: NotificationDto[] = [];
  private lessons: any[] = [];
  private patients: any[] = [];

  constructor(
    private websocketService: WebSocketService,
    private lessonStore: LessonStore,
    private patientStore: PatientStore
  ) {}

  ngOnInit(): void {
    // Получаем все уроки
    this.lessonsSub = this.lessonStore.lessons$.subscribe(lessons => {
      this.lessons = lessons;
      this.updateNotifications();
    });

    // Получаем всех пациентов
    this.patientsSub = this.patientStore.patients$.subscribe(patients => {
      this.patients = patients;
      this.updateNotifications();
    });

    // Подписка на уведомления
    this.wsSub = this.websocketService.messages$.subscribe(notifications => {
      this.notifications = notifications;
      this.updateNotifications();
    });
  }

  private updateNotifications() {
    if (!this.notifications.length || !this.lessons.length || !this.patients.length) return;

    const patientMap = new Map<number, string>();
    this.patients.forEach(p => patientMap.set(p.id, `${p.firstName} ${p.lastName}`));

    const lessonMap = new Map<number, any>();
    this.lessons.forEach(l => lessonMap.set(l.id, l));

    this.notificationsWithDetails = this.notifications.map(n => {
      const lesson = lessonMap.get(n.lessonNoteId);
      return {
        sendDate: n.sendDate,
        patients: n.patientsId.map((id: number) => ({
          id,
          name: patientMap.get(id) || 'Неизвестный пациент'
        })),
        message: n.message,
        lessonTopic: lesson ? lesson.topic : 'Без темы',
        lessonId: lesson ? lesson.id : n.lessonNoteId
      };
    });
  }

  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
    this.lessonsSub?.unsubscribe();
    this.patientsSub?.unsubscribe();
  }
}
