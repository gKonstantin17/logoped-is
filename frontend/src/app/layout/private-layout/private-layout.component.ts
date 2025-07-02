import { Component, OnInit } from '@angular/core';

import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {UserData, UserDataService} from '../../utils/services/user-data.service';
import {KeycloakService} from '../../utils/oauth2/bff/keycloak.service';
import {PatientService} from '../../utils/services/patient.service';
import {LessonService} from '../../utils/services/lesson.service';


@Component({
  selector: 'app-private-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink
  ],
  templateUrl: './private-layout.component.html',
  styleUrl: './private-layout.component.css'
})
export class PrivateLayoutComponent implements OnInit {
  userProfile?: UserData;

  constructor(private keycloakService: KeycloakService,
              private userService:UserDataService,
              private patientService: PatientService,
              private lessonService: LessonService,
              private router:Router,) {}

  // после авторизации получение профиля, синхронизация с бд,
  // получение данных о пациентах и занятиях
  ngOnInit(): void {
    this.keycloakService.requestUserProfile().subscribe({
      next: (profile: any) => {
        this.userProfile = {
          id: profile.id,
          firstName: profile.given_name,
          lastName: profile.family_name,
          email: profile.email,
          phone: profile.phone,
          role: profile.role
        };

        console.log('User profile loaded', this.userProfile);
        this.userService.setUserData(this.userProfile);

        this.keycloakService.isUserExist(this.userProfile).subscribe({
          next: exists => {
            console.log('User exists:', exists);
          },
          error: err => {
            console.error('Error checking if user exists:', err);
          }
        });

        this.loadInitialData(this.userProfile.id, this.userProfile.role);
      },
      error: err => {
        console.error('Error loading profile:', err);
      }
    });
  }

  private loadInitialData(userId: string, role: string) {
    if (role === 'user') {
      this.patientService.findByUser(userId).subscribe({
        next: patients => {
          this.userService.setPatients(patients); // новый метод
        },
        error: err => {
          console.error('Ошибка при загрузке пациентов пользователя:', err);
        }
      });

      this.lessonService.findByUser(userId).subscribe({
        next: lessons => {
          this.userService.setLessons(lessons); // новый метод
        },
        error: err => {
          console.error('Ошибка при загрузке занятий пользователя:', err);
        }
      });
    }

    if (role === 'logoped') {
      this.patientService.findByLogoped(userId).subscribe({
        next: patients => {
          this.userService.setPatients(patients);
        },
        error: err => {
          console.error('Ошибка при загрузке пациентов логопеда:', err);
        }
      });

      this.lessonService.findByLogoped(userId).subscribe({
        next: lessons => {
          this.userService.setLessons(lessons);
        },
        error: err => {
          console.error('Ошибка при загрузке занятий логопеда:', err);
        }
      });
    }
  }


  logout(): void {
    this.keycloakService.logoutAction().subscribe({
      next: () => {
        // После успешного logout — делаем редирект
        window.location.href = ''; // или на нужную тебе страницу
      },
      error: (err) => {
        console.error('Logout failed', err);
        this.router.navigate(['']);
      }
    });
  }

}
