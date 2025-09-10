import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule, NgForOf, NgIf} from '@angular/common';
import {
  trigger,
  transition,
  style,
  animate
} from '@angular/animations';
import {Router} from '@angular/router';
import {SpeechCardStore} from '../../../utils/stores/speechCard.store';
import {Observable} from 'rxjs';
@Component({
  selector: 'app-session',
  standalone: true,
  templateUrl: './diagnostic.component.html',
  imports: [
    FormsModule,
    NgIf,
    NgForOf,
    CommonModule
  ],

  styleUrls: ['./diagnostic.component.css'],
  animations: [
    trigger('slideInOut', [
      transition(':enter', [
        style({ transform: 'translateX(100%)', opacity: 0 }),
        animate('300ms ease-out', style({ transform: 'translateX(0)', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ transform: 'translateX(100%)', opacity: 0 }))
      ])
    ])
  ]
})
export class DiagnosticComponent implements OnInit{
  lessonId!: number;
  logopedId!: number;
  constructor(private router: Router, private speechCardStore: SpeechCardStore) {
    const nav = this.router.getCurrentNavigation();
    const state = nav?.extras?.state as any;

    if (state) {
      const dateOfBirth = new Date(state.dateOfBirth);
      const now = new Date();

      let years = now.getFullYear() - dateOfBirth.getFullYear();
      let months = now.getMonth() - dateOfBirth.getMonth();
      let days = now.getDate() - dateOfBirth.getDate();

      if (days < 0) months--;
      if (months < 0) {
        years--;
        months += 12;
      }

      this.speechCard.fullName = state.fullName;
      this.speechCard.dateOfBirth = dateOfBirth.toISOString().slice(0, 10);
      this.speechCard.age = `${years} лет ${months} мес.`;

      // 💡 Добавляем ID
      this.lessonId = state.lessonId;
      this.logopedId = state.logopedId;
    }
  }


  availableSpeechErrors$!: Observable<{ id: number; title: string; description: string }[]>;

  selectedError: { id: number; title: string; description: string } | null = null;

  ngOnInit() {
    this.availableSpeechErrors$ = this.speechCardStore.speechErrors$;
    this.speechCardStore.loadSpeechErrors(); // загружаем ошибки в стор
  }

  addSpeechError() {
    if (this.selectedError && !this.speechCard.speechErrors.some(e => e.id === this.selectedError!.id)) {
      this.speechCard.speechErrors.push(this.selectedError);
      this.selectedError = null;
    }
  }

  removeSpeechError(index: number) {
    this.speechCard.speechErrors.splice(index, 1);
  }
  currentTime = new Date().toLocaleTimeString();
  showSpeechCard = false;
  availableSounds: string[] = ['С', 'Сь', 'З', 'Зь', 'Ц', 'Ш', 'Ж', 'Ч', 'Щ', 'Р', 'Рь', 'Л', 'Ль'];
  correctionTypes: string[] = ['Поставлен', 'Введен в речь', 'Автоматизирован','Дифференцирован','Звукопроизношение в норме'];

  newCorrection = {
    sound: '',
    correction: ''
  };

  speechCard = {
    fullName: '',
    age: '',
    dateOfBirth: '',
    reason: '',
    stateOfHearning: '',
    anamnesis: '',
    generalMotor: '',
    fineMotor: '',
    articulatory: '',
    soundReproduction: '',
    soundComponition: '',
    speechChars: '',
    patientChars: '',
    speechErrors: [] as { id: number; title: string; description: string }[],
    soundCorrections: [] as { sound: string, correction: string }[]
  };

  addSoundCorrection() {
    if (this.newCorrection.sound && this.newCorrection.correction) {
      this.speechCard.soundCorrections.push({ ...this.newCorrection });
      this.newCorrection = { sound: '', correction: '' }; // Сброс
    }
  }

  removeSoundCorrection(index: number) {
    this.speechCard.soundCorrections.splice(index, 1);
  }
  toggleSpeechCard() {
    this.showSpeechCard = !this.showSpeechCard;
  }

  endSession() {
    // Логика завершения видеозанятия
    alert('Занятие завершено');
  }

  saveSpeechCard() {
    const payload = {
      reason: this.speechCard.reason,
      stateOfHearning: this.speechCard.stateOfHearning,
      anamnesis: this.speechCard.anamnesis,
      generalMotor: this.speechCard.generalMotor,
      fineMotor: this.speechCard.fineMotor,
      articulatory: this.speechCard.articulatory,
      soundReproduction: this.speechCard.soundReproduction,
      soundComponition: this.speechCard.soundComponition,
      speechChars: this.speechCard.speechChars,
      patientChars: this.speechCard.patientChars,
      speechErrors: this.speechCard.speechErrors.map(e => e.id),
      soundCorrections: this.speechCard.soundCorrections,
      lessonId: this.lessonId,
      logopedId: this.logopedId
    };

    this.speechCardStore.createWithDiagnostic(payload).subscribe({
      next: () => alert('Речевая карта сохранена успешно!'),
      error: (err) => console.error('Ошибка при сохранении карты:', err)
    });
  }

}
