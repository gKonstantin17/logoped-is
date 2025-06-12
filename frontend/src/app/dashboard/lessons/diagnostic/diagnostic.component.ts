import { Component, OnInit } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-diagnostic',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './diagnostic.component.html',
  styleUrl: './diagnostic.component.css'
})
export class DiagnosticComponent implements OnInit {
  currentTime: string = '';

  speechCard = {
    complaints: '',
    development: '',
    recommendations: ''
  };
  constructor(private router:Router) {
  }
  ngOnInit(): void {
    this.updateTime();
    setInterval(() => this.updateTime(), 1000);
  }

  updateTime() {
    const now = new Date();
    this.currentTime = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  saveSpeechCard() {
    console.log('Сохранена речевая карта:', this.speechCard);
    alert('Речевая карта сохранена');
  }

  endDiagnostic() {
    this.router.navigate(['dashboard/lessons'])
    // Здесь можно добавить логику перехода/сохранения
    // нужен переход в details
    // и перед этим опрос о занятии
  }
}
