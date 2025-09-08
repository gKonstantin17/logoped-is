import { Injectable } from '@angular/core';
import {Client, IFrame, IMessage} from '@stomp/stompjs';
import {BehaviorSubject, Observable} from 'rxjs';
import SockJS from 'sockjs-client';
import {NotificationService} from '../services/notification.service';

export interface NotificationDto {
  id:number,
  lessonNoteId: number,
  sendDate: string,
  message: string,
  received: boolean,
  recipientId: string,
  patientsId: number[]
}
@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: Client | null = null;  // STOMP client instance
  //private  messageSubject = new BehaviorSubject<string[]>([]);
  private  messageSubject = new BehaviorSubject<any[]>([]);
  public messages$ = this.messageSubject.asObservable();

  constructor(private notificationService: NotificationService) {}

  setMessages(notifications: any[]) {
    this.messageSubject.next(notifications);
  }
  loadInitMessages(userId: string) {
    this.notificationService.findByUser(userId).subscribe({
      next: notifications => this.setMessages(notifications),
      error: err => console.error('Ошибка при загрузке уведомлений:', err)
    });
  }
  // В WebSocketService
  loadMessages(userId: string): Observable<NotificationDto[]> {
    return this.notificationService.findByUser(userId);
  }

  markAsReceived(notificationId: number) {
    this.notificationService.markAsReceived(notificationId).subscribe({
      next: () => {
        const current = this.messageSubject.value;
        const updated = current.map(n =>
          n.id === notificationId ? { ...n, received: true } : n
        );
        this.messageSubject.next(updated);
      },
      error: err => console.error('Ошибка при пометке уведомления как прочитанного:', err)
    });
  }

  connect() {
    const socket = new SockJS('http://localhost:8380/ws');

    this.stompClient = new Client({
      webSocketFactory: () => socket,  // Use SockJS as the WebSocket factory
      reconnectDelay: 5000,  // Reconnect after 5 seconds if the connection is lost
      debug: (str:string) => {
        console.log(str);  // Log STOMP debug messages for troubleshooting
      },
    });

    // Success connect
    this.stompClient.onConnect = (frame:IFrame) => {
      console.log('Connected: ' + frame);  // Log connection success

      // Subscribe to the public topic '/topic/messages' to receive public messages
      // this.stompClient?.subscribe('/topic/messages', (message) => {
      //   // парсим поле, из объекта Message из Spring
      //   const parsedMessage = JSON.parse(message.body).messageContent;
      //   this.addMessage(parsedMessage);  // Add the received message to the message list
      // });
      this.stompClient?.subscribe('/user/queue/messages', (message: IMessage) => {
        const receivedEvent: NotificationDto = JSON.parse(message.body);
        this.addMessage(receivedEvent);
      });

    };

    // Handle errors reported by the STOMP server
    this.stompClient.onStompError = (frame:IFrame) => {
      console.error('Broker reported error: ' + frame.headers['message']);  // Log the error message
      console.error('Additional details: ' + frame.body);  // Log additional error details
    };

    // Activate the client (i.e., initiate the WebSocket connection)
    this.stompClient.activate();
  }
  // Function to send a message to the server via the WebSocket connection
  sendMessage(message: string) {
    if (this.stompClient?.connected) {  // проверка соединения
      this.stompClient?.publish({ // отправить
        destination: '/app/message',  // куда отправлять
        body: JSON.stringify({ messageContent: message })  // что отправлять
      });
    }
  }


  // Отправка в Backend
  // sendEvent(event: MyEvent) {
  //   if (this.stompClient?.connected) {
  //     this.stompClient.publish({
  //       destination: '/app/events',
  //       body: JSON.stringify(event)
  //     });
  //
  //   }
  // }


  // private addMessage(message: string) {
  //   // достаем messages: string[] = []; из компонента
  //   const currentMessages = this.messageSubject.value;
  //   console.log("MESSAGE ADDED : " + message)
  //   // пересоздаем новый массив, чтоб до ангуляра дошло, что изменения были
  //   this.messageSubject.next([...currentMessages, message]);  // Update the message list with the new message
  // }
  private addMessage(message: NotificationDto) {
    const currentMessages = this.messageSubject.value;
    this.messageSubject.next([...currentMessages, message]);
  }
}

// if (this.stompClient?.connected)
// эквивалетно
// if (this.stompClient && this.stompClient.connected)
// if (this.stompClient !== null && this.stompClient !== undefined && this.stompClient.connected)
