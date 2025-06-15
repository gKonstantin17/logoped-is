import {Component, ChangeDetectorRef, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FullCalendarModule } from '@fullcalendar/angular';
import {CalendarOptions, EventApi, EventInput} from '@fullcalendar/core';

import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from '@fullcalendar/timegrid';
import ruLocale from '@fullcalendar/core/locales/ru';
import {RouterLink} from '@angular/router';
import {UserDataService} from '../../utils/services/user-data.service';
import {PatientService} from '../../utils/services/patient.service';
import {LessonService} from '../../utils/services/lesson.service';

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
  constructor(private userDataService: UserDataService,
              private lessonService: LessonService,
              private cdr: ChangeDetectorRef) {
  }
  lessonDataList: any[] = [];
  currentRole: string | null = null;
  userId: string | null = null;
  ngOnInit(): void {
    this.userDataService.userData$.subscribe(user => {
      this.currentRole = user?.role || null;
      this.userId = user?.id || null;

      if (this.userId !== null) {
        if (this.currentRole === 'user') {
          this.lessonService.findByUser(this.userId).subscribe({
            next: (data) => {
              this.lessonDataList = data;
              console.log('Полученные дети:', this.lessonDataList);
              this.updateCalendarEvents();
            },
            error: (err) => {
              console.error('Ошибка при получении детей:', err);
            }
          });
        }

        if (this.currentRole === 'logoped') {
          this.lessonService.findByLogoped(this.userId).subscribe({
            next: (data) => {
              this.lessonDataList = data;
              console.log('Полученные дети:', this.lessonDataList);
              this.updateCalendarEvents();
            },
            error: (err) => {
              console.error('Ошибка при получении детей:', err);
            }
          });
        }
      }
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
  }


  selectedLesson: LessonData | null = null;
  isModalOpen = false;

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
