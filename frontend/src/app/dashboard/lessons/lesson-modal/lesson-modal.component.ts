import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {LessonData} from '../../../utils/services/lesson.service';
import {LessonStore} from '../../../utils/stores/lesson.store';

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
  constructor(private lessonStore:LessonStore) {
  }
  @Input() patientId!: number;
  @Input() hasSpeechCard: boolean | null = null;
  @Input() currentRole: string | null = null;


  @Output() closeModal = new EventEmitter<void>();
  @Output() confirmBooking = new EventEmitter<LessonData>();

  ngOnInit() {
    this.lessonStore.availableTimeSlots$.subscribe(slots => {
      this.availableTimeSlots = slots;
      this.selectedTime = '';
    });
  }
  step = 1;
  choice: 'diagnostic' | 'lesson' | null = null;
  selectedDate = '';
  selectedTime = '';
  lessonType = '';
  topic = '';
  description = '';
  homework = '';

  timeSlots: string[] = Array.from({length: 10}, (_, i) => `${10 + i}:00`);
  minDate = new Date().toISOString().split('T')[0];

  proceedToStep2(type: 'diagnostic' | 'lesson') {
    this.choice = type;
    this.step = 2;
  }
  proceedToStep3() {
    this.step = 3;
  }

  submit() {
    // Формируем строку с датой и временем
    const dateTime = `${this.selectedDate}T${this.selectedTime}:00`;

    if (this.choice === 'diagnostic') {
      this.confirmBooking.emit({
        type: 'Диагностика',
        topic: 'Первичная диагностика',
        description: 'Проведение первичной диагностики',
        dateOfLesson: dateTime,
        logopedId: null,
        homework: null,
        patientsId: []  // Добавим позже в handleBooking
      });
    } else {
      this.confirmBooking.emit({
        type: this.lessonType || 'Коррекция речи',
        topic: this.topic || 'Занятие с логопедом',
        description: this.description || 'Скоро появится',
        dateOfLesson: dateTime,
        logopedId: null,
        homework: this.homework || null,
        patientsId: [] // Добавим позже в handleBooking
      });
    }
  }
  get allTimeSlots(): string[] {
    return Array.from({ length: 10 }, (_, i) => `${(10 + i).toString().padStart(2, '0')}:00`);
  }
  availableTimeSlots: string[] = [];
  onDateChange(dateStr: string) {
    this.selectedDate = dateStr;
    const date = new Date(dateStr);

    if (!this.patientId) return;

    this.lessonStore.checkTime(this.patientId, date);
  }




  cancel() {
    this.closeModal.emit();
  }
}
