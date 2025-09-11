import { Injectable } from '@angular/core';
import {BehaviorSubject, tap} from 'rxjs';
import {SpeechCardService} from '../services/speech-card.service';


@Injectable({
  providedIn: 'root' // сервис будет синглтоном на всё приложение
})
export class SpeechCardStore {
  constructor(private speechCardService:SpeechCardService) { }

  private currentSpeechCardSubject = new BehaviorSubject<any | null>(null);
  currentSpeechCard$ = this.currentSpeechCardSubject.asObservable();

  private speechErrorsSubject = new BehaviorSubject<{ id: number; title: string; description: string }[]>([]);
  speechErrors$ = this.speechErrorsSubject.asObservable();

  // Метод загрузки речевой карты и обновления BehaviorSubject
  loadSpeechCard(patientId: number) {
    return this.speechCardService.findByPatient(patientId).pipe(
      tap({
        next: data => this.currentSpeechCardSubject.next(data),
        error: () => this.currentSpeechCardSubject.next(null)
      })
    );
  }
  loadSpeechErrors() {
    return this.speechCardService.findAllError().pipe(
      tap({
        next: data => this.speechErrorsSubject.next(data),
        error: () => this.speechErrorsSubject.next([])
      })
    ).subscribe(); // или возвращай Observable и подписывайся из компонента
  }

  findAllError() {
    return this.speechCardService.findAllError();
  }

  createWithDiagnostic(data: any) {
    return this.speechCardService.createWithDiagnostic(data);
  }
  updateCorrections(data:any) {
    return this.speechCardService.updateCorrections(data);
  }

  findByPatient(patientId: number) {
    return this.speechCardService.findByPatient(patientId);
  }
  findCorrectionsByPatient(patientId: number) {
    return this.speechCardService.findCorrectionsByPatient(patientId)
  }
}

