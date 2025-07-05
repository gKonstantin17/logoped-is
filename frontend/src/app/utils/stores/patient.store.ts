import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, of, tap} from 'rxjs';
import {PatientChangeData, PatientService} from '../services/patient.service';
import {catchError} from 'rxjs/operators';


@Injectable({
  providedIn: 'root' // сервис будет синглтоном на всё приложение
})
export class PatientStore {
  constructor(private patientService:PatientService) { }

  private patientsSubject = new BehaviorSubject<any[]>([]);
  patients$ = this.patientsSubject.asObservable();

  setPatients(patients: any[]) {
    this.patientsSubject.next(patients);
  }

  refresh(userId: string, role: string): void {
    const result = role === 'logoped' ?
      this.patientService.findByLogoped(userId) :
      this.patientService.findByUser(userId);

    result.subscribe({
      next: patients => this.setPatients(patients),
      error: err => console.error('Ошибка при обновлении занятий:', err)
    });
  }
  create(data: any) {
    this.patientService.create(data).subscribe({
      next: created => {
        const current = this.patientsSubject.getValue();
        this.patientsSubject.next([...current, created]);
      },
      error: err => {
        console.error('Ошибка при создании пациента:', err);
      }
    });
  }

  update(data: PatientChangeData, id: number) {
    this.patientService.update(data, id).subscribe({
      next: updated => {
        const current = this.patientsSubject.getValue();
        const updatedList = current.map(p =>
          p.id === id ? { ...p, ...updated } : p
        );
        this.patientsSubject.next(updatedList);
      },
      error: err => {
        console.error('Ошибка при обновлении пациента:', err);
      }
    });
  }

  delete(patientId: number) {
    this.patientService.delete(patientId).subscribe({
      next: () => {
        const current = this.patientsSubject.getValue();
        const updatedList = current.filter(p => p.id !== patientId);
        this.patientsSubject.next(updatedList);
      },
      error: err => {
        console.error('Ошибка при удалении пациента:', err);
      }
    });
  }
  existsSpeechCard(patientId: number): Observable<boolean> {
    return this.patientService.existsSpeechCard(patientId);
  }
}

