import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {CheckAvailableTime, LessonData, LessonService} from '../services/lesson.service';


@Injectable({
  providedIn: 'root'
})
export class LessonStore {
  constructor(private lessonService:LessonService) { }


  // список занятий у пациентов пользователя
  private lessonsSubject = new BehaviorSubject<any[]>([]);
  lessons$ = this.lessonsSubject.asObservable();

  // занятие в details
  private currentLessonSubject = new BehaviorSubject<any | null>(null);
  currentLesson$ = this.currentLessonSubject.asObservable();

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
  loadLesson(id: number) {
    this.lessonService.findWithFk(id).subscribe({
      next: lesson => this.currentLessonSubject.next(lesson),
      error: err => console.error('Ошибка при загрузке урока:', err)
    });
  }
  cancel(id: number) {
    this.lessonService.cancelLesson(id).subscribe({
      next: lesson => this.currentLessonSubject.next(lesson),
      error: err => console.error('Ошибка при отмене урока:', err)
    });
  }
  changeDate(lessonId:number, newDate:Date) {
    this.lessonService.changeDateLesson(lessonId,newDate).subscribe({
      next: lesson => this.currentLessonSubject.next(lesson),
      error: err => console.error('Ошибка при переносе урока:', err)
    })
  }
  private availableTimeSlotsSubject = new BehaviorSubject<string[]>([]);
  availableTimeSlots$ = this.availableTimeSlotsSubject.asObservable();

  checkTime(patientId: number, date: Date): void {
    this.lessonService.checkTimeLesson(patientId, date).subscribe({
      next: slots => {
        console.log('Слоты:', slots);
        this.availableTimeSlotsSubject.next(slots.availableTime || []);
      },
      error: err => {
        console.error('Ошибка при проверке времени:', err);
        alert('Ошибка при получении времени');
        this.availableTimeSlotsSubject.next([]);
      }
    });
  }




}

