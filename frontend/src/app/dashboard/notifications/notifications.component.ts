import { Component, OnInit, OnDestroy } from '@angular/core';
import { WebSocketService, NotificationDto } from '../../utils/websocket/WebSocketService';
import { Subscription } from 'rxjs';
import {DatePipe, NgForOf} from '@angular/common';

@Component({
  selector: 'app-notifications',
  standalone: true,
  templateUrl: './notifications.component.html',
  imports: [
    DatePipe,
    NgForOf
  ],
  styleUrl: './notifications.component.css'
})
export class NotificationsComponent implements OnInit, OnDestroy {
  notifications: NotificationDto[] = [];
  private sub: Subscription | null = null;

  constructor(private websocketService: WebSocketService) {}

  ngOnInit(): void {
    // Подписка на BehaviorSubject сервиса
    this.sub = this.websocketService.messages$.subscribe({
      next: msgs => {
        this.notifications = msgs;
      },
      error: err => console.error('Ошибка при получении уведомлений:', err)
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
}
