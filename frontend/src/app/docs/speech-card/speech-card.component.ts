import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SpeechCardService } from '../../utils/services/speech-card.service';
import {DatePipe, NgIf} from '@angular/common';

@Component({
  selector: 'app-speech-card',
  standalone: true,
  templateUrl: './speech-card.component.html',
  styleUrls: ['./speech-card.component.css'],
  imports: [
    DatePipe,
    NgIf
  ]
})
export class SpeechCardComponent implements OnInit {
  speechCardData: any;

  constructor(
    private route: ActivatedRoute,
    private speechCardService: SpeechCardService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const patientId = +params['id'];
      if (patientId) {
        this.speechCardService.findByPatient(patientId).subscribe({
          next: (data) => {
            this.speechCardData = data;
            console.log('Карта речи:', data);
          },
          error: (err) => {
            console.error('Ошибка при получении речевой карты:', err);
          }
        });
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
