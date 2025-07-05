import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {LessonService} from '../services/lesson.service';


@Injectable({
  providedIn: 'root' // сервис будет синглтоном на всё приложение
})
export class LessonStore {
  constructor(private lessonService:LessonService) { }

  private lessonsSubject = new BehaviorSubject<any[]>([]);
  lessons$ = this.lessonsSubject.asObservable();

  setLessons(lessons: any[]) {
    this.lessonsSubject.next(lessons);
  }

  refreshLessons(userId: string, role: string): void {
    const obs = role === 'logoped' ?
      this.lessonService.findByLogoped(userId) :
      this.lessonService.findByUser(userId);

    obs.subscribe({
      next: lessons => this.setLessons(lessons),
      error: err => console.error('Ошибка при обновлении занятий:', err)
    });
  }

}

