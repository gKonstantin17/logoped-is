import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-change-date-modal',
  imports: [
    FormsModule,
    NgIf,
    NgForOf
  ],
  standalone:true,
  templateUrl: './change-date-modal.component.html',
  styleUrl: './change-date-modal.component.css'
})
export class ChangeDateModalComponent {
  @Input() visible = false;
  @Output() close = new EventEmitter<void>();
  @Output() submit = new EventEmitter<Date>();

  selectedDate: string = '';
  selectedTime: string = '';
  minDate: string = new Date().toISOString().split('T')[0];

  timeSlots = ['10:00', '11:00', '12:00', '13:00', '14:00', '15:00'];

  confirm() {
    if (this.selectedDate && this.selectedTime) {
      const combined = new Date(`${this.selectedDate}T${this.selectedTime}:00`);
      this.submit.emit(combined);
    }
  }

  cancel() {
    this.close.emit();
  }
}
