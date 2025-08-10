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

  private hiddenPatientsSubject = new BehaviorSubject<any[]>([]);
  hiddenPatients$ = this.hiddenPatientsSubject.asObservable();

  private lastUserId: string | null = null;
  private lastRole: string | null = null;
  setPatients(patients: any[]) {
    this.patientsSubject.next(patients);
  }
  setHiddenPatients(patients: any[]) {
    this.hiddenPatientsSubject.next(patients);
  }

  refresh(userId: string, role: string): void {
    this.lastUserId = userId;
    this.lastRole = role;
    const result = role === 'logoped' ?
      this.patientService.findByLogopedWithSC(userId) :
      this.patientService.findByUser(userId);

    result.subscribe({
      next: patients => {
        const visible = patients.filter(p => !p.isHidden);
        const hidden = patients.filter(p => p.isHidden);

        this.setPatients(visible);
        this.setHiddenPatients(hidden);
      },
      error: err => console.error('Ошибка при обновлении занятий:', err)
    });
  }
  refreshCached(): void {
    if (this.lastUserId && this.lastRole) {
      this.refresh(this.lastUserId, this.lastRole);
    }
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

  hide(patientId: number) {
    this.patientService.hide(patientId).subscribe({
      next: () => this.refreshCached(),
      error: err => console.error('Ошибка при удалении пациента:', err)
    });
  }

  restore(patientId: number) {
    this.patientService.restore(patientId).subscribe({
      next: () => this.refreshCached(),
      error: err => console.error('Ошибка при восстановлении пациента:', err)
    });
  }

  existsSpeechCard(patientId: number): Observable<boolean> {
    return this.patientService.existsSpeechCard(patientId);
  }
}

