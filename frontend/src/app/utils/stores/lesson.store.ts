import { Injectable } from '@angular/core';
import {BehaviorSubject, tap} from 'rxjs';
import {LessonData, LessonService} from '../services/lesson.service';


@Injectable({
  providedIn: 'root'
})
export class LessonStore {
  constructor(private lessonService:LessonService) { }

  private lessonsSubject = new BehaviorSubject<any[]>([]);
  lessons$ = this.lessonsSubject.asObservable();

  setLessons(lessons: any[]) {
    this.lessonsSubject.next(lessons);
  }

  refresh(userId: string, role: string): void {
    const result = role === 'logoped' ?
      this.lessonService.findByLogoped(userId) :
      this.lessonService.findByUser(userId);

    result.subscribe({
      next: lessons => this.setLessons(lessons),
      error: err => console.error('Ошибка при обновлении занятий:', err)
    });
  }

  create(data: LessonData) {
    return this.lessonService.createLesson(data).pipe(
      tap(newLesson => {
        const currentLessons = this.lessonsSubject.getValue();
        this.setLessons([...currentLessons, newLesson]);
      })
    );
  }
  findWithFk(id: number) {
    return this.lessonService.findWithFk(id);
  }

}

