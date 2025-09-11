import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {LessonStatusDto} from '../../../utils/services/lesson.service';
import {LessonStatus} from '../../../utils/enums/lesson-status.enum';
import {LessonStore} from '../../../utils/stores/lesson.store';
import {ConfirmModalComponent} from './confirm-modal.component';
import {NgIf} from '@angular/common';
import {SpeechCardStore} from '../../../utils/stores/speechCard.store';
import {CorrectionItem, CorrectionModalComponent} from './components/correction-modal.component';

@Component({
  selector: 'app-session',
  standalone: true,
  imports: [NgIf, ConfirmModalComponent, CorrectionModalComponent],
  templateUrl: './session.component.html',
  styleUrl: './session.component.css'
})
export class SessionComponent implements OnInit {
  constructor(private router: Router,
              private route: ActivatedRoute,
              private lessonStore: LessonStore,
              private speechCardStore: SpeechCardStore) {
  }


  currentTime: string = '';
  lessonId!: number;
  status!:string;
  patientId!: number;
  showConfirmModal = false;
  confirmMessage = '';
  confirmAction!: () => void;
  statusUpdated = false; // флаг, чтобы скрывать кнопки после ответа

  corrections: CorrectionItem[] = [];
  showCorrectionModal = false;
  ngOnInit() {
    this.updateTime();
    setInterval(() => this.updateTime(), 1000);

    this.route.queryParams.subscribe(params => {
      this.lessonId = +params['id'];
      this.status = params['status'];
      this.patientId = params['patientId'];
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

  // endSession() {
  //
  //   this.speechCardStore.findCorrectionsByPatient(this.patientId).subscribe({
  //     next: (corrections) => {
  //       console.log('Коррекции пациента:', corrections);
  //       this.corrections = corrections;
  //     },
  //     error: (err) => console.error('Ошибка при получении коррекций:', err)
  //   });
  // }
  endSession() {

    // const dto : LessonStatusDto = {
    //   id: this.lessonId,
    //   status: LessonStatus.COMPLETED
    // }


    this.speechCardStore.findCorrectionsByPatient(this.patientId).subscribe({
      next: (corrections) => {
        console.log('Коррекции пациента:', corrections);
        // сохраняем и открываем новое модальное окно
        this.corrections = corrections.map((c:any) => ({ ...c, selectedCorrection: c.correction }));
        this.showCorrectionModal = true;
      },
      error: (err) => console.error('Ошибка при получении коррекций:', err)
    });
    // this.lessonStore.updateStatus(dto);
    // this.router.navigate(['dashboard/lessons'])
  }

  onCorrectionsSubmit(updated: CorrectionItem[]) {
    // проверяем, есть ли хотя бы одно изменение
    const hasChange = updated.some(c => c.selectedCorrection !== c.correction);

    if (!hasChange) {
      console.log('Коррекции не изменились, ничего не отправляем');
      this.showCorrectionModal = false;
      return; // ничего не делаем
    }

    // формируем DTO со всеми выбранными коррекциями
    const dto = {
      patientId: Number(this.patientId),
      lessonId: Number(this.lessonId),
      updatedCorrections: updated.map(c => ({
        sound: c.sound,
        correction: c.selectedCorrection
      }))
    };

    console.log('DTO для отправки:', dto);

    this.speechCardStore.updateCorrections(dto).subscribe({
      next: card => {
        console.log("card:")
        console.log(card)
      },
      error: (err) => console.error('Ошибка при создании карты:', err)
    })
    this.showCorrectionModal = false;

    // Теперь обновляем статус урока и переходим
    const statusDto: LessonStatusDto = { id: this.lessonId, status: LessonStatus.COMPLETED };
    this.lessonStore.updateStatus(statusDto);
    this.router.navigate(['dashboard/lessons']);
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
