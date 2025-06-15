import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {UserDataService} from '../../../utils/services/user-data.service';
import {LessonService} from '../../../utils/services/lesson.service';

@Component({
  selector: 'app-details',
  standalone: true,
  imports: [
    NgIf,
    RouterLink,
    DatePipe,
    NgForOf
  ],
  templateUrl: './details.component.html',
  styleUrl: './details.component.css'
})
export class DetailsComponent implements OnInit {
  lessonId!: number;
  selectedTab: 'lesson' | 'about' | 'description' = 'lesson';

  lesson: any|null;
  constructor(private userDataService: UserDataService,
              private route: ActivatedRoute,
              private router: Router,
              private lessonService:LessonService) {}
  currentRole: string | null = null;
  ngOnInit() {
    this.lessonId = +this.route.snapshot.paramMap.get('id')!;
    this.userDataService.userData$.subscribe(user => {
      this.currentRole = user?.role || null;
    });

    this.lessonService.findWithFk(this.lessonId).subscribe({
      next: (data) => {
        this.lesson = data;
        console.log('Полученное занятие:', this.lesson);
      },
      error: (err) => {
        console.error('Ошибка при получении детей:', err);
      }
    });
  }

  openSession() {
    this.router.navigate(['/dashboard/session']);
  }
  openDiagnostic() {
    const patient = this.lesson?.patients?.[0];
    if (!patient) return;

    this.router.navigate(['/dashboard/diagnostic'], {
      state: {
        fullName: `${patient.firstName} ${patient.lastName}`,
        dateOfBirth: patient.dateOfBirth,
        lessonId: this.lesson?.id,
        logopedId: this.lesson?.logoped?.id
      }
    });
  }


}
