import { Injectable } from '@angular/core';
import {BehaviorSubject, tap} from 'rxjs';
import {SpeechCardService} from '../services/speech-card.service';


@Injectable({
  providedIn: 'root' // сервис будет синглтоном на всё приложение
})
export class SpeechCardStore {
  constructor(private speechCardService:SpeechCardService) { }

  private speechCardSubject = new BehaviorSubject<any[]>([]);
  speechCards$ = this.speechCardSubject.asObservable();

  setLessons(lessons: any[]) {
    this.speechCardSubject.next(lessons);
  }
  findAllError() {
    return this.speechCardService.findAllError();
  }

  createWithDiagnostic(data: any) {
    return this.speechCardService.createWithDiagnostic(data);
  }

  findByPatient(patientId: number) {
    return this.speechCardService.findByPatient(patientId);
  }
}

