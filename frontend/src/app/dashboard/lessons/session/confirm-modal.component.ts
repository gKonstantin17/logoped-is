import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirm-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="modal-overlay">
      <div class="modal-content">
        <p>{{ message }}</p>
        <div class="modal-actions">
          <button (click)="confirm.emit()">Подтвердить</button>
          <button (click)="cancel.emit()">Отмена</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .modal-overlay {
      position: fixed;
      inset: 0;
      background: rgba(0,0,0,0.5);
      display: flex;
      justify-content: center;
      align-items: center;
    }
    .modal-content {
      background: white;
      padding: 24px;
      border-radius: 12px;
      text-align: center;
    }
    .modal-actions button {
      margin: 0 8px;
      padding: 8px 16px;
      border: none;
      border-radius: 6px;
      cursor: pointer;
    }
  `]
})
export class ConfirmModalComponent {
  @Input() message!: string;
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
}
