import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {AsyncPipe, DatePipe, NgForOf, NgIf} from '@angular/common';
import {UserDataStore} from '../../../utils/stores/user-data.store';
import {LessonStore} from '../../../utils/stores/lesson.store';
import {Observable} from 'rxjs';
import {ChangeDateModalComponent} from './change-date-modal/change-date-modal.component';
import {LessonStatus, LessonStatusLabels} from '../../../utils/enums/lesson-status.enum';
import {FormsModule} from '@angular/forms';
import {LessonTypesEnum, LessonTypesEnumLabels} from '../../../utils/enums/lesson-types.enum';
import {PatientStore} from '../../../utils/stores/patient.store';

@Component({
  selector: 'app-details',
  standalone: true,
  imports: [
    NgIf,
    RouterLink,
    DatePipe,
    NgForOf,
    AsyncPipe,
    ChangeDateModalComponent,
    FormsModule
  ],
  templateUrl: './details.component.html',
  styleUrl: './details.component.css'
})
export class DetailsComponent implements OnInit {
  lessonId!: number;
  selectedTab: 'lesson' | 'about' | 'description' = 'lesson';
  currentRole: string | null = null;
  lesson$!: Observable<any>;
  showRescheduleModal = false;
  protected readonly LessonStatus = LessonStatus;
  lessonTypeLabels = LessonTypesEnumLabels;
  lessonTypeKeys = Object.values(LessonTypesEnum);

  isEditMode = false;
  editableLesson: any = null;
  private originalLesson: any = null; // храним исходные данные
  private originalPatients: any[] = [];
  lessonStatuses = Object.values(LessonStatus);
  patients: any[] = [];

  constructor(private userDataStore: UserDataStore,
              private lessonStore: LessonStore,
              private patientStore: PatientStore,
              private route: ActivatedRoute,
              private router: Router) {}
  ngOnInit() {
    this.lesson$ = this.lessonStore.currentLesson$;
    this.lessonId = +this.route.snapshot.paramMap.get('id')!;

    this.userDataStore.userData$.subscribe(user => {
      this.currentRole = user?.role || null;
      this.lessonStore.loadLesson(this.lessonId);
    });

    this.lesson$.subscribe(lesson => {
      if (lesson) {
        this.editableLesson = { ...lesson }; // копия для редактирования
      }
    });
  }

  openSession() {
    this.lesson$.subscribe(lesson => {
      this.router.navigate(['/dashboard/session'], {
        queryParams: { id: this.lessonId, status: lesson.status }
      });
    }).unsubscribe(); // сразу отписываемся
  }
  openDiagnostic() {
    this.lesson$.subscribe(lesson => {
      const patient = lesson?.patients?.[0];
      if (!patient) return;

      this.router.navigate(['/dashboard/diagnostic'], {
        state: {
          fullName: `${patient.firstName} ${patient.lastName}`,
          dateOfBirth: patient.dateOfBirth,
          lessonId: lesson?.id,
          logopedId: lesson?.logoped?.id
        }
      });
    }).unsubscribe();  // Не забудь отписаться или использовать async pipe в шаблоне!
  }
  getStatusLabel(status: LessonStatus): string {
    return LessonStatusLabels[status];
  }

  cancelLesson() {
    const confirmed = confirm('Отменить занятие?');
    if (!confirmed) return;

    this.lessonStore.cancel(this.lessonId);
    this.lessonStore.loadLesson(this.lessonId);
  }

  onReschedule(newDate: Date) {
    this.lessonStore.changeDate(this.lessonId, newDate);
    this.showRescheduleModal = false;

    this.lessonStore.loadLesson(this.lessonId);

    // Перенаправление на календарь с нужной датой
    this.router.navigate(['/dashboard/calendar'], {
      queryParams: {
        date: newDate.toISOString()  // Преобразуем дату в формат ISO
      }
    });


  }
  toggleEditMode() {
    this.isEditMode = !this.isEditMode;

    if (this.isEditMode) {
      // Глубокое копирование для отката
      this.originalLesson = JSON.parse(JSON.stringify(this.editableLesson));
      this.originalPatients = JSON.parse(JSON.stringify(this.patients));

      console.log('editableLesson перед редактированием:', this.editableLesson);

      const logopedId = this.editableLesson?.logoped?.id;
      if (logopedId) {
        this.loadPatientsForLogoped(logopedId);
      }
    }
  }






  private loadPatientsForLogoped(logopedId: string) {
    this.patientStore.findByLogoped(logopedId).subscribe({
      next: patients => {
        this.patients = patients; // сохраняем локально
        console.log('Загруженные пациенты:', this.patients);
      },
      error: err => console.error('Ошибка при загрузке пациентов:', err)
    });
  }
  autoResize(event: Event) {
    const textarea = event.target as HTMLTextAreaElement;
    textarea.style.height = 'auto'; // сброс высоты
    textarea.style.height = textarea.scrollHeight + 'px'; // подстраиваем под контент
  }
  selectedPatientToAdd: any = null;

  availablePatients(): any[] {
    if (!this.patients) return [];
    return this.patients.filter(p =>
      !this.editableLesson.patients?.some((ep:any) => ep.id === p.id)
    );
  }

  addPatient() {
    if (this.selectedPatientToAdd) {
      if (!this.editableLesson.patients) this.editableLesson.patients = [];
      this.editableLesson.patients.push(this.selectedPatientToAdd);
      this.selectedPatientToAdd = null; // сброс выбора
    }
  }

  removePatient(patient: any) {
    this.editableLesson.patients = this.editableLesson.patients.filter((p:any) => p.id !== patient.id);
  }

  cancelEdit() {
    this.isEditMode = false;

    // Откатываем изменения с глубокой копией
    if (this.originalLesson) {
      this.editableLesson = JSON.parse(JSON.stringify(this.originalLesson));
    }
    if (this.originalPatients) {
      this.patients = JSON.parse(JSON.stringify(this.originalPatients));
    }

    console.log('Изменения откатились:', this.editableLesson, this.patients);
  }

  saveChanges() {
    if (!this.editableLesson) return;
    //this.lessonStore.update(this.lessonId, this.editableLesson);
    this.isEditMode = false;
    this.lessonStore.loadLesson(this.lessonId);
  }

}
