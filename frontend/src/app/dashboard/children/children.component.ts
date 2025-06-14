import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule, NgForOf} from '@angular/common';
import {UserDataService} from '../../services/user-data.service';
import {PatientService} from '../../services/patient.service';
import {RouterLink} from '@angular/router';

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
  ]
})
export class ChildrenComponent implements OnInit {
  childrenForms: { firstName: string; lastName: string; birthDate: string; speechErrors: string[]; speechCorrection: string[] }[] = [];
  addedChildren: {id:number; firstName: string; secondName: string; dateOfBirth: string; speechErrors: string[]; speechCorrection: string[] }[] = [];
  currentRole: string | null = null;
  userId: number | null = null;
  patientsOfUser: any[] = [];
  constructor(private userDataService: UserDataService,private patientService: PatientService) {}

  ngOnInit() {
    this.userDataService.userData$.subscribe(user => {
      this.currentRole = user?.role || null;
      this.userId = user?.id || null;

      if (this.userId !== null) {
        if (this.currentRole === 'user') {
          this.patientService.findByUser(this.userId).subscribe({
            next: (data) => {
              this.patientsOfUser = data;
              console.log('Полученные дети:', this.patientsOfUser);
            },
            error: (err) => {
              console.error('Ошибка при получении детей:', err);
            }
          });
        }
        if (this.currentRole === 'logoped') {
          this.patientService.findByLogoped(this.userId).subscribe({
            next: (data) => {
              this.addedChildren = data;
              console.log('Полученные дети:', this.patientsOfUser);
            },
            error: (err) => {
              console.error('Ошибка при получении детей:', err);
            }
          })
        }
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
      secondName: newChild.lastName,
      dateOfBirth: newChild.birthDate,
      userId: this.userId
    };

    this.patientService.create(payload).subscribe({
      next: (createdPatient) => {
        this.patientsOfUser.push(createdPatient); // теперь сразу добавляется в карточки
        this.childrenForms.splice(index, 1); // удалить форму
      },
      error: (err) => {
        console.error('Ошибка при добавлении ребёнка:', err);
      }
    });
  }

  removeForm(index: number) {
    this.childrenForms.splice(index, 1);
  }

  removeChild(index: number) {
    const patient = this.patientsOfUser[index];
    if (!patient || !patient.id) return;

    const confirmed = confirm(`Вы уверены, что хотите удалить ${patient.firstName} ${patient.secondName}?`);
    if (confirmed) {
      this.patientService.delete(patient.id).subscribe({
        next: () => {
          this.patientsOfUser.splice(index, 1);
          console.log('Пациент удалён');
        },
        error: (err) => {
          console.error('Ошибка при удалении пациента:', err);
        }
      });
    }
  }

  sortColumn: 'speechErrors' | 'speechCorrection' | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';

  get sortedChildren() {
    if (!this.sortColumn) return this.addedChildren;

    return [...this.addedChildren].sort((a, b) => {
      const aValue = (a[this.sortColumn!] || []).join(', ');
      const bValue = (b[this.sortColumn!] || []).join(', ');

      if (this.sortDirection === 'asc') {
        return aValue.localeCompare(bValue);
      } else {
        return bValue.localeCompare(aValue);
      }
    });
  }

  toggleSort(column: 'speechErrors' | 'speechCorrection') {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
  }

  selectedSpeechError: string | null = null;
  get availableSpeechErrors(): string[] {
    const allErrors = this.addedChildren.flatMap(child => child.speechErrors);
    return Array.from(new Set(allErrors));
  }

  get filteredChildren() {
    if (!this.selectedSpeechError) return this.addedChildren;

    return this.addedChildren.filter(child =>
      child.speechErrors.includes(this.selectedSpeechError!)
    );
  }

  editingPatient: any | null = null;
  editFormData: { firstName: string; secondName: string; dateOfBirth: string } = {
    firstName: '',
    secondName: '',
    dateOfBirth: ''
  };
  startEdit(patient: any) {
    const formattedDate = this.formatDateForInput(patient.dateOfBirth);
    this.editingPatient = patient;
    this.editFormData = {
      firstName: patient.firstName,
      secondName: patient.secondName,
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
  }
  saveEdit() {
    if (this.editingPatient && this.editingPatient.id) {
      this.patientService.update(this.editFormData, this.editingPatient.id).subscribe({
        next: (updated) => {
          Object.assign(this.editingPatient, updated);
          this.editingPatient = null;
        },
        error: (err) => {
          console.error('Ошибка при обновлении ребёнка:', err);
        }
      });
    }
  }

}
