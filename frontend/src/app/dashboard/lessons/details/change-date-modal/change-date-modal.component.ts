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

  generateTimeSlots(startHour: number, endHour: number): string[] {
    return Array.from({ length: endHour - startHour + 1 }, (_, i) =>
      `${(startHour + i).toString().padStart(2, '0')}:00`
    );
  }

  timeSlots = this.generateTimeSlots(10, 19);

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
