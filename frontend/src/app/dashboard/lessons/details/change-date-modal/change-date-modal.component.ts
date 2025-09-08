import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {LessonStore} from '../../../../utils/stores/lesson.store';

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
export class ChangeDateModalComponent implements OnChanges {
  constructor(private lessonStore: LessonStore) {}

  @Input() visible = false;
  @Input() patientId!: number; // <--- Добавляем id пациента для проверки слотов
  @Output() close = new EventEmitter<void>();
  @Output() submit = new EventEmitter<Date>();

  selectedDate: string = '';
  selectedTime: string = '';
  minDate: string = new Date().toISOString().split('T')[0];

  availableTimeSlots: string[] = [];
  timeSlots: string[] = [];

  ngOnChanges(changes: SimpleChanges) {
    // при открытии модалки сбрасываем выбранные данные
    if (changes['visible'] && this.visible) {
      this.selectedDate = '';
      this.selectedTime = '';
      this.timeSlots = this.generateTimeSlots(10, 19);
      this.availableTimeSlots = [];
    }
  }

  generateTimeSlots(startHour: number, endHour: number): string[] {
    return Array.from({ length: endHour - startHour + 1 }, (_, i) =>
      `${(startHour + i).toString().padStart(2, '0')}:00`
    );
  }

  onDateChange() {
    if (!this.patientId || !this.selectedDate) return;
    const date = new Date(this.selectedDate);
    this.lessonStore.checkTime(this.patientId, date);
    this.lessonStore.availableTimeSlots$.subscribe(slots => {
      this.availableTimeSlots = slots;
    });
  }

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
