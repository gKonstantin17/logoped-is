import { Component, EventEmitter, Output } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-lesson-modal',
  standalone: true,
  templateUrl: './lesson-modal.component.html',
  imports: [
    CommonModule, FormsModule
  ],
  styleUrls: ['./lesson-modal.component.css']
})
export class LessonModalComponent {
  step = 1;
  choice: 'diagnostic' | 'lesson' | null = null;
  selectedDate: string = '';
  selectedTime: string = '';

  @Output() closeModal = new EventEmitter<void>();
  @Output() confirmBooking = new EventEmitter<{type: string, date: string, time: string}>();

  timeSlots: string[] = Array.from({length: 13}, (_, i) => `${8 + i}:00`);
  minDate = new Date().toISOString().split('T')[0]; // формат: '2025-06-12'
  proceedToStep2(type: 'diagnostic' | 'lesson') {
    this.choice = type;
    this.step = 2;
  }

  submit() {
    this.confirmBooking.emit({ type: this.choice!, date: this.selectedDate, time: this.selectedTime });
    this.closeModal.emit();
  }

  cancel() {
    this.closeModal.emit();
  }
}
