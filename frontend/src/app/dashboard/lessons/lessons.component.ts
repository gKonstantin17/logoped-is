import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {RouterLink} from "@angular/router";
import {LessonModalComponent} from './lesson-modal/lesson-modal.component';

@Component({
  selector: 'app-lessons',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, LessonModalComponent],
  templateUrl: './lessons.component.html',
  styleUrls: ['./lessons.component.css']
})
export class LessonsComponent {
  selectedChildId: number = 1; // выбран первый ребенок по умолчанию

  childrenData = [
    { id: 1, firstName: 'Сеня', secondName: 'Иванов', dateOfBirth: '15.01.2015' },
    { id: 2, firstName: 'Зоя', secondName: 'Семенова', dateOfBirth: '15.07.2015' },
  ];

  lessonData = {
    id: 4,
    type: 'диагностика',
    topic: 'Первичная диагностика',
    description: 'string',
    dateOfLesson: '2025-06-01T15:58:36.786+03:00',
    logopedId: null,
    homeworkId: null,
    patientsId: [1], // дети с занятиями
  };

  get selectedChildLessons() {
    if (!this.selectedChildId) return [];

    // Если в lessonData есть patientId совпадающий с выбранным ребенком - возвращаем lessonData в массиве
    return this.lessonData.patientsId.includes(this.selectedChildId)
      ? [this.lessonData]
      : [];
  }

  get selectedChild() {
    return this.childrenData.find(child => child.id === this.selectedChildId);
  }

  showModal = false;

  openModal() {
    this.showModal = true;
  }

  handleBooking(data: { type: string, date: string, time: string }) {
    console.log('Бронирование:', data);
    // Пример перехода на календарь
    window.location.href = '/dashboard/calendar';
  }
}

