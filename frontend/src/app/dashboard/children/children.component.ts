import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule, NgForOf} from '@angular/common';
import {UserDataService} from '../../services/user-data.service';

@Component({
  selector: 'app-children',
  standalone: true,
  templateUrl: './children.component.html',
  styleUrls: ['./children.component.css'],
  imports: [
    FormsModule,
    NgForOf,
    CommonModule
  ]
})
export class ChildrenComponent implements OnInit {
  childrenForms: { firstName: string; lastName: string; birthDate: string; speechErrors: string[]; speechCorrection: string[] }[] = [];
  addedChildren: { firstName: string; lastName: string; birthDate: string; speechErrors: string[]; speechCorrection: string[] }[] = [];
  currentRole: string | null = null;

  constructor(private userDataService: UserDataService) {}

  ngOnInit() {
    this.userDataService.userData$.subscribe(user => {
      this.currentRole = user?.role || null;
    });

    // Заглушка: имитация данных для логопеда
    if (this.currentRole === 'logoped') {
      this.addedChildren = [
        { firstName: 'Анна', lastName: 'Иванова', birthDate: '2015-03-10', speechErrors:['ОНР 1','Картавость'], speechCorrection:['постановка Ж', 'коррекция Р'] },
        { firstName: 'Максим', lastName: 'Петров', birthDate: '2016-07-22', speechErrors:['ОНР 2','Картавость'], speechCorrection:['постановка Л', 'коррекция Р'] }
      ];
    }
  }
  addForm() {
    this.childrenForms.push({ firstName: '', lastName: '', birthDate: '', speechErrors:[], speechCorrection:[] });
  }

  addChild(index: number) {
    const child = this.childrenForms[index];
    this.addedChildren.push(child);
    this.childrenForms.splice(index, 1);
  }

  removeForm(index: number) {
    this.childrenForms.splice(index, 1);
  }

  removeChild(index: number) {
    this.addedChildren.splice(index, 1);
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
}
