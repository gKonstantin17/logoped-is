import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {RouterLink} from "@angular/router";
import {LessonModalComponent} from './lesson-modal/lesson-modal.component';
import {UserDataService} from '../../utils/services/user-data.service';
import {PatientService} from '../../utils/services/patient.service';
import {LessonData, LessonService} from '../../utils/services/lesson.service';

@Component({
  selector: 'app-lessons',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, LessonModalComponent],
  templateUrl: './lessons.component.html',
  styleUrls: ['./lessons.component.css']
})
export class LessonsComponent implements OnInit {
  selectedChildId: number = 0; // отображать занятия у всех пациентов

  childrenData: any[] = [];
  constructor(private userDataService: UserDataService,
              private patientService: PatientService,
              private lessonService: LessonService) {}
  currentRole: string | null = null;
  userId: string | null = null;
  lessonDataList: any[] = [];
  ngOnInit() {
    this.userDataService.userData$.subscribe(user => {
      this.currentRole = user?.role || null;
      this.userId = user?.id || null;

      if (this.userId !== null) {
        if (this.currentRole === 'user') {
          this.patientService.findByUser(this.userId).subscribe({
            next: (data) => {
              this.childrenData = data;
              console.log('Полученные дети:', this.childrenData);
            },
            error: (err) => {
              console.error('Ошибка при получении детей:', err);
            }
          });
          this.lessonService.findByUser(this.userId).subscribe({
            next: (data) => {
              this.lessonDataList = data;
              console.log('Полученные дети:', this.lessonDataList);
            },
            error: (err) => {
              console.error('Ошибка при получении детей:', err);
            }
          });

        }
        if (this.currentRole === 'logoped') {
          this.patientService.findByLogoped(this.userId).subscribe({
            next: (data) => {
              this.childrenData = data;
              console.log('Полученные дети:', this.childrenData);
            },
            error: (err) => {
              console.error('Ошибка при получении детей:', err);
            }
          })
          this.lessonService.findByLogoped(this.userId).subscribe({
            next: (data) => {
              this.lessonDataList = data;
              console.log('Полученные дети:', this.lessonDataList);
            },
            error: (err) => {
              console.error('Ошибка при получении детей:', err);
            }
          });


        }
      }
    });
  }

  get upcomingLessons() {
    const now = new Date();
    return this.selectedChildLessons.filter(lesson => new Date(lesson.dateOfLesson) >= now);
  }

  get pastLessons() {
    const now = new Date();
    return this.selectedChildLessons.filter(lesson => new Date(lesson.dateOfLesson) < now);
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

    this.patientService.existsSpeechCard(this.selectedChildId).subscribe({
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

    const lessonData: LessonData = {
      ...data,
      dateOfLesson: new Date(data.dateOfLesson).toISOString(),
      logopedId: data.type === "Диагностика" ? null : this.userId,
      patientsId: [this.selectedChildId]
    };


    this.lessonService.createLesson(lessonData).subscribe({
      next: () => {
        this.showToast('Занятие успешно добавлено');
        this.showModal = false;
      },
      error: () => this.showToast('Ошибка при создании занятия')
    });
  }






}

