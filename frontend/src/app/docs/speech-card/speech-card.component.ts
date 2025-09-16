import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {CommonModule, DatePipe, NgIf} from '@angular/common';
import {SpeechCardStore} from '../../utils/stores/speechCard.store';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-speech-card',
  standalone: true,
  templateUrl: './speech-card.component.html',
  styleUrls: ['./speech-card.component.css'],
  imports: [
    DatePipe,
    NgIf,
    CommonModule
  ]
})
export class SpeechCardComponent implements OnInit {
  speechCardData$!: Observable<any>;
  constructor(
    private route: ActivatedRoute,
    private speechCardStore: SpeechCardStore
  ) {}

  ngOnInit() {
    this.speechCardData$ = this.speechCardStore.currentSpeechCard$;

    this.route.queryParams.subscribe(params => {
      const speechCardId = params.hasOwnProperty('speechCardId') ? Number(params['speechCardId']) : null;
      const patientId = params.hasOwnProperty('patientId') ? Number(params['patientId']) : null;

      if (speechCardId) {
        this.speechCardStore.findById(speechCardId).subscribe();
      } else if (patientId) {
        this.speechCardStore.loadSpeechCard(patientId).subscribe();
      } else {
        alert('Не указан идентификатор для загрузки речевой карты');
      }
    });
  }


  getAge(birthDateStr: string): string {
    const birthDate = new Date(birthDateStr);
    const now = new Date();

    let years = now.getFullYear() - birthDate.getFullYear();
    let months = now.getMonth() - birthDate.getMonth();
    let days = now.getDate() - birthDate.getDate();

    if (days < 0) months--;
    if (months < 0) {
      years--;
      months += 12;
    }

    return `${years} лет ${months} мес.`;
  }


}
