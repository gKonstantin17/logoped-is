import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-session',
  standalone: true,
  imports: [],
  templateUrl: './session.component.html',
  styleUrl: './session.component.css'
})
export class SessionComponent implements OnInit {
  constructor(private router: Router) {
  }
  currentTime: string = '';

  ngOnInit() {
    this.updateTime();
    setInterval(() => this.updateTime(), 1000);
  }

  updateTime() {
    const now = new Date();
    this.currentTime = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  endSession() {
   this.router.navigate(['dashboard/lessons'])
    // Здесь можно добавить логику перехода/сохранения
    // нужен переход в details
    // и перед этим опрос о занятии
  }
}
