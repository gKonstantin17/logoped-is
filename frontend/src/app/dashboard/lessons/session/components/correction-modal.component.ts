import { Component, EventEmitter, Input, Output } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgForOf} from '@angular/common';

export interface CorrectionItem {
  id: number;
  sound: string;
  correction: string;
  selectedCorrection?: string; // для выбора из типов
}

@Component({
  selector: 'app-correction-modal',
  standalone: true,
  template: `
    <div class="modal-backdrop">
      <div class="modal-content">
        <h3>Выберите состояние коррекции</h3>
        <div *ngFor="let item of corrections">
          <label>{{item.sound}}</label>
          <select [(ngModel)]="item.selectedCorrection">
            <option *ngFor="let type of correctionTypes" [value]="type">{{type}}</option>
          </select>
        </div>
        <button (click)="confirm()">Подтвердить</button>
        <button (click)="cancel.emit()">Отмена</button>
      </div>
    </div>
  `,
  imports: [
    FormsModule,
    NgForOf
  ],
  styles: [`
    .modal-backdrop {
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .modal-content {
      background: #fff;
      padding: 20px;
      border-radius: 8px;
      min-width: 300px;
    }

    select {
      margin-left: 10px;
      margin-bottom: 10px;
    }
  `]
})
export class CorrectionModalComponent {
  @Input() corrections: CorrectionItem[] = [];
  @Output() submit = new EventEmitter<CorrectionItem[]>();
  @Output() cancel = new EventEmitter<void>();

  correctionTypes: string[] = ['Поставлен', 'Введен в речь', 'Автоматизирован','Дифференцирован','Звукопроизношение в норме'];

  confirm() {
    this.submit.emit(this.corrections);
  }
}
