import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { BackendService } from '../oauth2/backend/backend.service';
import { HttpMethod } from '../oauth2/model/RequestBFF';
import { environment } from '../../../environments/environment';

export interface NotificationDto {
  id: number;
  lessonNoteId: number;
  sendDate: string;
  message: string;
  received: boolean;
  recipientId: string;
  patientsId: number[];
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private baseUrl = `${environment.MS_NOTIFICATION_URL}/notification`;

  constructor(private backend: BackendService) {}

  findByUser(userId: string): Observable<NotificationDto[]> {
    return this.backend.createOperation(HttpMethod.POST, `${this.baseUrl}/find-messages`, userId);
  }
  markAsReceived(notificationId: number): Observable<any> {
    return this.backend.createOperation(HttpMethod.PUT, `${this.baseUrl}/receive`, notificationId);
  }
}
