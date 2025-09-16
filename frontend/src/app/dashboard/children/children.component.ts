import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule, NgForOf} from '@angular/common';
import {Router, RouterLink} from '@angular/router';
import {UserDataStore} from '../../utils/stores/user-data.store';
import {PatientStore} from '../../utils/stores/patient.store';
import {ChangeDateModalComponent} from '../lessons/details/change-date-modal/change-date-modal.component';

@Component({
  selector: 'app-children',
  standalone: true,
  templateUrl: './children.component.html',
  styleUrls: ['./children.component.css'],
  imports: [
    FormsModule,
    NgForOf,
    CommonModule,
    RouterLink,
    ChangeDateModalComponent,
  ]
})
export class ChildrenComponent implements OnInit {
  childrenForms: {
    firstName: string;
    lastName: string;
    birthDate: string;
    speechErrors: {title: string; description: string}[];
    speechCorrection: {sound: string; correction: string}[]
  }[] = [];

  addedChildren: {
    id: number;
    firstName: string;
    lastName: string;
    dateOfBirth: string;
    speechErrors: {title: string; description: string}[];
    soundCorrections: {sound: string; correction: string}[]
  }[] = [];
  currentRole: string | null = null;
  userId: string | null = null;
  patientsOfUser: any[] = [];
  hiddenPatients: any[] = [];
  constructor(
    private userDataStore: UserDataStore,
    private patientStore: PatientStore,
    private router: Router
  ) {}
  // TODO нужен ли userDataService?
  // TODO косяк с данными для логопеда
  ngOnInit() {
    this.userDataStore.userData$.subscribe(user => {
      this.currentRole = user?.role || null;
      this.userId = user?.id || null;

      this.patientStore.patients$.subscribe(data => {
        this.patientsOfUser = data;
        if (this.currentRole === 'logoped')
          this.addedChildren = data;
      });

      this.patientStore.hiddenPatients$.subscribe(hidden => {
        this.hiddenPatients = hidden;
      });

      if (this.userId && this.currentRole) {
        this.patientStore.refresh(this.userId, this.currentRole);
      }
    });
  }
  addForm() {
    this.childrenForms.push({ firstName: '', lastName: '', birthDate: '', speechErrors:[], speechCorrection:[] });
  }

  addChild(index: number) {
    const newChild = this.childrenForms[index];

    const payload = {
      firstName: newChild.firstName,
      lastName: newChild.lastName,
      dateOfBirth: newChild.birthDate,
      userId: this.userId
    };

    this.patientStore.create(payload);
  }

  removeForm(index: number) {
    this.childrenForms.splice(index, 1);
  }

  removeChild(index: number) {
    const patient = this.patientsOfUser[index];
    if (!patient || !patient.id) return;

    const confirmed = confirm(`Вы уверены, что хотите скрыть ${patient.firstName} ${patient.lastName}?`);
    if (confirmed) {
      this.patientStore.hide(patient.id);
    }
    this.editingPatient = null;
    this.editingPatientIndex = null;
  }
  goToSpeechCard(patientId: number) {
    this.patientStore.existsSpeechCard(patientId)
      .subscribe((exists: boolean) => {
        if (exists) {
          this.router.navigate(['/dashboard/speechcard'], { queryParams: { patientId: patientId } });
        } else {
          alert('Речевая карта для этого пациента не найдена.');
        }
      });
  }
  sortColumn: 'speechErrors' | 'soundCorrections' | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';

  // get sortedChildren() {
  //   if (!this.sortColumn) return this.addedChildren;
  //
  //   return [...this.addedChildren].sort((a, b) => {
  //     const aValue = (a[this.sortColumn!] || []).join(', ');
  //     const bValue = (b[this.sortColumn!] || []).join(', ');
  //
  //     if (this.sortDirection === 'asc') {
  //       return aValue.localeCompare(bValue);
  //     } else {
  //       return bValue.localeCompare(aValue);
  //     }
  //   });
  // }

  toggleSort(column: 'speechErrors' | 'soundCorrections') {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
  }

  selectedSpeechError: string | null = null;
  get availableSpeechErrors(): string[] {
    // Собираем все уникальные названия speechErrors
    const allErrors = this.addedChildren.flatMap(child => child.speechErrors.map(e => e.title));
    return Array.from(new Set(allErrors));
  }

  get filteredChildren() {
    if (!this.selectedSpeechError) return this.addedChildren;

    return this.addedChildren.filter(child =>
      child.speechErrors.some(e => e.title === this.selectedSpeechError)
    );
  }

  editingPatient: any | null = null;
  editFormData: { firstName: string; lastName: string; dateOfBirth: string } = {
    firstName: '',
    lastName: '',
    dateOfBirth: ''
  };
  editingPatientIndex: number | null = null;

  startEdit(patient: any, index: number) {
    const formattedDate = this.formatDateForInput(patient.dateOfBirth);
    this.editingPatient = patient;
    this.editingPatientIndex = index;
    this.editFormData = {
      firstName: patient.firstName,
      lastName: patient.lastName,
      dateOfBirth: formattedDate
    };
  }
  formatDateForInput(dateString: string): string {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }
  cancelEdit() {
    this.editingPatient = null;
    this.editingPatientIndex = null;
  }

  saveEdit() {
    if (this.editingPatient && this.editingPatient.id) {
      this.patientStore.update(this.editFormData, this.editingPatient.id);
    }
    this.editingPatient = null;
    this.editingPatientIndex = null;
  }


  showRestoreModal = false;

  openRestoreModal() {
    this.showRestoreModal = true;
  }

  closeRestoreModal() {
    this.showRestoreModal = false;
  }

  restorePatient(patientId: number) {
    this.patientStore.restore(patientId);
    // Обновим список скрытых пациентов, если нужно
    if (this.userId && this.currentRole) {
      this.patientStore.refresh(this.userId, this.currentRole);
    }
  }

  getAge(birthDateStr: string): string {
    const birthDate = new Date(birthDateStr);
    const now = new Date();

    let years = now.getFullYear() - birthDate.getFullYear();
    let months = now.getMonth() - birthDate.getMonth();
    let days = now.getDate() - birthDate.getDate();

    if (days < 0) months--;
    if (months < 0) {
      years--;
      months += 12;
    }

    return `${years} лет ${months} мес.`;
  }

  goToLesson(patientId: number) {
    this.router.navigate(['/dashboard/lessons'], { queryParams: { childId: patientId } });
  }

}
