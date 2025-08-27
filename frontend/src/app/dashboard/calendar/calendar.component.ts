import {Component, ChangeDetectorRef, OnInit, ViewEncapsulation} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FullCalendarModule } from '@fullcalendar/angular';
import {CalendarOptions, EventApi, EventInput} from '@fullcalendar/core';

import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from '@fullcalendar/timegrid';
import ruLocale from '@fullcalendar/core/locales/ru';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {LessonFullData} from '../../utils/services/lesson.service';
import {UserDataStore} from '../../utils/stores/user-data.store';
import {LessonStore} from '../../utils/stores/lesson.store';
import {LessonStatus, LessonStatusLabels} from '../../utils/enums/lesson-status.enum';

interface LessonData {
  id: number;
  type: string;
  topic: string;
  description: string;
  dateOfLesson: string; // ISO format
  logopedId: number | null;
  homeworkId: number | null;
  patientsId: number[];
}

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule, FullCalendarModule, RouterLink],
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class CalendarComponent implements OnInit {
  constructor(private userDataStore: UserDataStore,
              private lessonStore: LessonStore,
              private cdr: ChangeDetectorRef,
              private route: ActivatedRoute) {
  }
  lessonDataList: LessonFullData[] = [];
  currentRole: string | null = null;
  userId: string | null = null;
  currentDate: Date = new Date();
  calendarOptions: CalendarOptions | null = null;
  // TODO нужен ли userDataService?
  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['date']) {
        this.currentDate = new Date(params['date']);
      } else {
        this.currentDate = new Date();
      }

      this.calendarOptions = {
        plugins: [interactionPlugin, timeGridPlugin],
        initialView: 'timeGridWeek',
        locale: ruLocale,
        nowIndicator: true,
        allDaySlot: false,
        slotMinTime: '10:00:00',
        slotMaxTime: '20:00:00',
        headerToolbar: {
          left: 'prev,next today',
          center: 'title',
          right: 'timeGridWeek'
        },
        visibleRange: (currentDate) => {
          const start = new Date(currentDate);
          start.setDate(start.getDate() - 3);
          const end = new Date(currentDate);
          end.setDate(end.getDate() + 3);
          return { start, end };
        },
        events: this.getCalendarEvents(),
        eventClick: this.handleEventClick.bind(this),
        initialDate: this.currentDate,
      };

      this.userDataStore.userData$.subscribe(user => {
        this.currentRole = user?.role || null;
        this.userId = user?.id || null;

        this.lessonStore.lessons$.subscribe(data => {
          this.lessonDataList = data;
          console.log(this.lessonDataList);
          this.updateCalendarEvents();
        });

        this.lessonStore.refresh(this.userId!, this.currentRole!);
      });
    });
  }

  get homeworkTask(): string {
    return this.selectedLesson?.homework?.task || 'не задано';
  }


  selectedLesson: LessonFullData | null = null;
  isModalOpen = false;
  get logopedFullName(): string {
    if (!this.selectedLesson?.logoped) return 'не назначен';
    return `${this.selectedLesson.logoped.firstName} ${this.selectedLesson.logoped.lastName}`;
  }

  handleEventClick(arg: any) {
    const eventId = parseInt(arg.event.id, 10);
    this.selectedLesson = this.lessonDataList.find(lesson => lesson.id === eventId) || null;
    this.isModalOpen = true;
  }
  updateCalendarEvents() {
    if (this.calendarOptions) {
      this.calendarOptions = {
        ...this.calendarOptions,
        events: this.getCalendarEvents()
      };
      this.cdr.detectChanges();
    }
  }

  getCalendarEvents(): EventInput[] {
    return this.lessonDataList.map(lesson => {
      const start = new Date(lesson.dateOfLesson);
      const end = new Date(start);
      end.setHours(start.getHours() + 1);

      const isCancelled =
        lesson.status === LessonStatus.CANCELED_BY_CLIENT ||
        lesson.status === LessonStatus.CANCELED_BY_LOGOPED;

      const patientName = lesson.patients.length > 0
        ? `${lesson.patients[0].firstName} ${lesson.patients[0].lastName}`
        : 'Без пациента';

      return {
        id: String(lesson.id),
        title: `${patientName}: ${lesson.topic}`,
        start: start.toISOString(),
        end: end.toISOString(),
        className: isCancelled ? 'event-cancelled' : '',
      };

    });
  }









}
