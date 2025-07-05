import {Component, ChangeDetectorRef, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FullCalendarModule } from '@fullcalendar/angular';
import {CalendarOptions, EventApi, EventInput} from '@fullcalendar/core';

import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from '@fullcalendar/timegrid';
import ruLocale from '@fullcalendar/core/locales/ru';
import {RouterLink} from '@angular/router';
import {LessonFullData} from '../../utils/services/lesson.service';
import {UserDataStore} from '../../utils/stores/user-data.store';
import {LessonStore} from '../../utils/stores/lesson.store';

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
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {
  constructor(private userDataStore: UserDataStore,
              private lessonStore: LessonStore,
              private cdr: ChangeDetectorRef) {
  }
  lessonDataList: LessonFullData[] = [];
  currentRole: string | null = null;
  userId: string | null = null;

  // TODO нужен ли userDataService?
  ngOnInit(): void {
    this.userDataStore.userData$.subscribe(user => {
      this.currentRole = user?.role || null;
      this.userId = user?.id || null;

      this.lessonStore.lessons$.subscribe(data => {
        this.lessonDataList = data;
      });
    });

    this.calendarOptions.events = this.lessonDataList.map(lesson => {
      const start = new Date(lesson.dateOfLesson);
      const end = new Date(start);
      end.setHours(start.getHours() + 1); // по умолчанию +1 час

      return {
        id: String(lesson.id),
        title: lesson.topic,
        start: start.toISOString(),
        end: end.toISOString()
      } as EventInput;
    });

    // нужно вызвать detectChanges, если Angular не отследит изменения
    this.cdr.detectChanges();
    this.lessonStore.refresh(this.userId!, this.currentRole!);
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
    this.calendarOptions.events = this.lessonDataList.map(lesson => {
      const start = new Date(lesson.dateOfLesson);
      const end = new Date(start);
      end.setHours(start.getHours() + 1); // длительность 1 час

      return {
        id: String(lesson.id),
        title: lesson.topic,
        start: start.toISOString(),
        end: end.toISOString()
      } as EventInput;
    });

    // Явно детектим изменения
    this.cdr.detectChanges();
  }


  calendarOptions: CalendarOptions = {
    plugins: [interactionPlugin, timeGridPlugin],
    initialView: 'timeGridWeek',
    locale: ruLocale,
    nowIndicator: true,
    allDaySlot: false,
    slotMinTime: '09:00:00',
    slotMaxTime: '20:00:00',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'timeGridWeek'
    },
    visibleRange(currentDate) {
      const start = new Date(currentDate);
      start.setDate(start.getDate() - 3);
      const end = new Date(currentDate);
      end.setDate(end.getDate() + 3);
      return { start, end };
    },
    events: [], // заполним в ngOnInit
    eventClick: this.handleEventClick.bind(this),
  };



}
