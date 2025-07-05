import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {PatientService} from '../services/patient.service';


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

  refreshPatients(userId: string, role: string): void {
    const obs = role === 'logoped' ?
      this.patientService.findByLogoped(userId) :
      this.patientService.findByUser(userId);

    obs.subscribe({
      next: patients => this.setPatients(patients),
      error: err => console.error('Ошибка при обновлении занятий:', err)
    });
  }
}

