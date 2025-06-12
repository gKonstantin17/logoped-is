import {Component, ChangeDetectorRef, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FullCalendarModule } from '@fullcalendar/angular';
import {CalendarOptions, EventApi, EventInput} from '@fullcalendar/core';

import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from '@fullcalendar/timegrid';
import ruLocale from '@fullcalendar/core/locales/ru';
import {RouterLink} from '@angular/router';

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
  lessonDataList: LessonData[] = [
    {
      id: 4,
      type: 'диагностика',
      topic: 'Первичная диагностика',
      description: 'string',
      dateOfLesson: '2025-06-01T15:58:36.786+03:00',
      logopedId: null,
      homeworkId: null,
      patientsId: [1]
    },
    {
      id: 5,
      type: 'терапия',
      topic: 'Логопедическое занятие',
      description: 'string',
      dateOfLesson: '2025-06-02T10:00:00.000+03:00',
      logopedId: null,
      homeworkId: null,
      patientsId: [1]
    }
  ];

  selectedLesson: LessonData | null = null;
  isModalOpen = false;

  handleEventClick(arg: any) {
    const eventId = parseInt(arg.event.id, 10);
    this.selectedLesson = this.lessonDataList.find(lesson => lesson.id === eventId) || null;
    this.isModalOpen = true;
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

  constructor(private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
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
}
