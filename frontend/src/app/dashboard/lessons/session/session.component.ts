import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {LessonStatusDto} from '../../../utils/services/lesson.service';
import {LessonStatus} from '../../../utils/enums/lesson-status.enum';
import {LessonStore} from '../../../utils/stores/lesson.store';
import {ConfirmModalComponent} from './confirm-modal.component';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-session',
  standalone: true,
  imports: [NgIf,ConfirmModalComponent],
  templateUrl: './session.component.html',
  styleUrl: './session.component.css'
})
export class SessionComponent implements OnInit {
  constructor(private router: Router,
              private route: ActivatedRoute,
              private lessonStore: LessonStore) {
  }
  currentTime: string = '';
  lessonId!: number;
  status!:string;

  showConfirmModal = false;
  confirmMessage = '';
  confirmAction!: () => void;
  statusUpdated = false; // флаг, чтобы скрывать кнопки после ответа

  ngOnInit() {
    this.updateTime();
    setInterval(() => this.updateTime(), 1000);

    this.route.queryParams.subscribe(params => {
      this.lessonId = +params['id'];
      this.status = params['status'];
    });
  }

  updateTime() {
    const now = new Date();
    this.currentTime = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  confirmStartLesson() {
    this.confirmMessage = 'Начать урок?';
    this.confirmAction = () => this.setLessonInProgress();
    this.showConfirmModal = true;
  }

  confirmNoShow() {
    this.confirmMessage = 'Пациент не пришел?';
    this.confirmAction = () => this.setNoShowClient();
    this.showConfirmModal = true;
  }

  confirmEndSession() {
    this.confirmMessage = 'Закончить занятие?';
    this.confirmAction = () => this.endSession();
    this.showConfirmModal = true;
  }

  onConfirm() {
    this.showConfirmModal = false;
    if (this.confirmAction) this.confirmAction();
  }

  endSession() {
    const dto : LessonStatusDto = {
      id: this.lessonId,
      status: LessonStatus.COMPLETED
    }
    this.lessonStore.updateStatus(dto);
    this.router.navigate(['dashboard/lessons'])
    // Здесь можно добавить логику перехода/сохранения
    // нужен переход в details
    // и перед этим опрос о занятии
  }

  setLessonInProgress() {
    const dto : LessonStatusDto = {
      id: this.lessonId,
      status: LessonStatus.IN_PROGRESS
    }
    this.lessonStore.updateStatus(dto);
    this.statusUpdated = true;
  }

  setNoShowClient() {
    const dto : LessonStatusDto = {
      id: this.lessonId,
      status: LessonStatus.NO_SHOW_CLIENT
    }
    this.lessonStore.updateStatus(dto);
    this.statusUpdated = true;
  }
}
