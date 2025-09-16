import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {LessonModalComponent} from './lesson-modal/lesson-modal.component';
import {LessonData, LessonService} from '../../utils/services/lesson.service';
import {UserDataStore} from '../../utils/stores/user-data.store';
import {PatientStore} from '../../utils/stores/patient.store';
import {LessonStore} from '../../utils/stores/lesson.store';
import {LessonStatus} from '../../utils/enums/lesson-status.enum';

@Component({
  selector: 'app-lessons',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, LessonModalComponent],
  templateUrl: './lessons.component.html',
  styleUrls: ['./lessons.component.css']
})
export class LessonsComponent implements OnInit {
  selectedChildId: number = 0; // отображать занятия у всех пациентов


  constructor(private router: Router,
              private route: ActivatedRoute,
              private userDataStore: UserDataStore,
              private patientStore: PatientStore,
              private lessonStore:LessonStore) {}
  currentRole: string | null = null;
  userId: string | null = null;
  lessonDataList: any[] = [];
  childrenData: any[] = [];

  // TODO нужен ли userDataService?
  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const paramId = parseInt(params['childId'], 10);
      if (!isNaN(paramId)) {
        this.selectedChildId = paramId; // 👈 установить выбранного ребёнка
      }
    });

    this.userDataStore.userData$.subscribe(user => {
      this.currentRole = user?.role || null;
      this.userId = user?.id || null;

      this.patientStore.patients$.subscribe(data => {
        this.childrenData = data;
      });

      this.lessonStore.lessons$.subscribe(data => {
        this.lessonDataList = data;
      });


    });
    // занятия могут создать как User так и Logoped, нужны актуальные данные
    this.lessonStore.refresh(this.userId!, this.currentRole!);
  }

  private cancelledStatuses: LessonStatus[] = [
    LessonStatus.CANCELED_BY_CLIENT,
    LessonStatus.CANCELED_BY_LOGOPED
  ];

  get upcomingLessons() {
    const now = new Date();
    return this.selectedChildLessons.filter(
      lesson => !this.cancelledStatuses.includes(lesson.status) &&
        new Date(lesson.dateOfLesson) >= now
    );
  }

  get pastLessons() {
    const now = new Date();
    return this.selectedChildLessons.filter(
      lesson => !this.cancelledStatuses.includes(lesson.status) &&
        new Date(lesson.dateOfLesson) < now
    );
  }

  get cancelledLessons() {
    return this.selectedChildLessons.filter(
      lesson => this.cancelledStatuses.includes(lesson.status)
    );
  }


  get selectedChildLessons() {
    if (this.selectedChildId === 0) {
      return this.lessonDataList;
    }

    return this.lessonDataList.filter(lesson =>
      lesson.patients?.some((patient: any) => patient.id === this.selectedChildId)
    );
  }




  get selectedChild() {
    return this.childrenData.find(child => child.id === this.selectedChildId);
  }


  showModal = false;
  hasSpeechCard: boolean | null = null;
  openModal() {
    if (this.selectedChildId === 0) {
      this.showToast('Пожалуйста, выберите ребёнка перед подбором занятия.');
      return;
    }

    this.patientStore.existsSpeechCard(this.selectedChildId).subscribe({
      next: (result) => {
        this.showModal = true;
        this.hasSpeechCard = result; // true или false
      },
      error: (err) => {
        console.error('Ошибка проверки карты:', err);
        this.showToast('Не удалось проверить наличие карты. Попробуйте позже.');
      }
    });
  }

  toastMessage: string | null = null;

  showToast(message: string) {
    this.toastMessage = message;
    setTimeout(() => {
      this.toastMessage = null;
    }, 3000); // 3 секунды
  }


  handleBooking(data: LessonData) {
    if (this.selectedChildId === 0) {
      this.showToast('Пожалуйста, выберите ребёнка перед записью занятия.');
      return;
    }
    const patient = this.childrenData.find(child => child.id === this.selectedChildId);
    const logopedId = data.type === "Диагностика" ? null : patient?.logopedId || null;
    const lessonData: LessonData = {
      ...data,
      dateOfLesson: new Date(data.dateOfLesson).toISOString(),
      logopedId: logopedId,
      patientsId: [this.selectedChildId]
    };


    this.lessonStore.create(lessonData).subscribe({
      next: () => {
        this.showToast('Занятие успешно добавлено');
        this.showModal = false;
        this.router.navigate(['dashboard/calendar'], { queryParams: { date: lessonData.dateOfLesson } });
      },
      error: () => this.showToast('Ошибка при создании занятия')
    });
  }









}

