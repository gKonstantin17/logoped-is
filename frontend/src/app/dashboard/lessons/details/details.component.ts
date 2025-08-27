import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {AsyncPipe, DatePipe, NgForOf, NgIf} from '@angular/common';
import {UserDataStore} from '../../../utils/stores/user-data.store';
import {LessonStore} from '../../../utils/stores/lesson.store';
import {Observable} from 'rxjs';
import {ChangeDateModalComponent} from './change-date-modal/change-date-modal.component';
import {LessonStatus, LessonStatusLabels} from '../../../utils/enums/lesson-status.enum';

@Component({
  selector: 'app-details',
  standalone: true,
  imports: [
    NgIf,
    RouterLink,
    DatePipe,
    NgForOf,
    AsyncPipe,
    ChangeDateModalComponent
  ],
  templateUrl: './details.component.html',
  styleUrl: './details.component.css'
})
export class DetailsComponent implements OnInit {
  lessonId!: number;
  selectedTab: 'lesson' | 'about' | 'description' = 'lesson';

  constructor(private userDataStore: UserDataStore,
              private lessonStore: LessonStore,
              private route: ActivatedRoute,
              private router: Router) {}

  currentRole: string | null = null;
  lesson$!: Observable<any>;
  showRescheduleModal = false;

  ngOnInit() {
    this.lesson$ = this.lessonStore.currentLesson$;
    this.lessonId = +this.route.snapshot.paramMap.get('id')!;
    this.userDataStore.userData$.subscribe(user => {
      this.currentRole = user?.role || null;
      this.lessonStore.loadLesson(this.lessonId);
    });
  }

  openSession() {
    this.router.navigate(['/dashboard/session']);
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

  protected readonly LessonStatusLabels = LessonStatusLabels;
  protected readonly LessonStatus = LessonStatus;
}
