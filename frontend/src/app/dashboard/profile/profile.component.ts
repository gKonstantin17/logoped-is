  import {Component, OnInit} from '@angular/core';
  import { CommonModule } from '@angular/common';
  import { FormsModule } from '@angular/forms';
  import {UserData} from '../../utils/services/user-data.service';
  import {UserDataStore} from '../../utils/stores/user-data.store';
  import {LessonStore} from '../../utils/stores/lesson.store';
  import {PatientStore} from '../../utils/stores/patient.store';
  import {ChartConfiguration, ChartData, ChartOptions, ChartType} from 'chart.js';
  import {LessonStatus, LessonStatusLabels} from '../../utils/enums/lesson-status.enum';
  import {NgChartsModule} from 'ng2-charts';

  @Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, FormsModule, NgChartsModule],
    templateUrl: './profile.component.html',
    styleUrl: './profile.component.css'
  })
  export class ProfileComponent implements OnInit {
    activeTab: 'account' | 'statistics' | 'help' = 'account';
    lessons: any[] = [];

    selectedPatientId: number | null = null;
    filteredLessons: any[] = [];
    data: UserData | null = null;

    patients: any[] = []; // список пациентов для select

    // Chart
    public pieChartData: ChartData<'pie', number[], string | string[]> = {
      labels: [],
      datasets: [{ data: [] }]
    };
    public pieChartOptions: ChartOptions<'pie'> = {
      responsive: true
    };
    public pieChartType: ChartConfiguration<'pie', number[], string | string[]>['type'] = 'pie';
    constructor(
      private userDataStore: UserDataStore,
      private lessonStore: LessonStore,
      private patientStore: PatientStore
    ) {}

    ngOnInit() {
      // данные пользователя
      this.userDataStore.userData$.subscribe(user => {
        if (user) {
          this.data = user;
          // загружаем уроки для этого пользователя
          this.lessonStore.refresh(user.id, 'user');
        }
      });

      // уроки
      this.lessonStore.lessons$.subscribe(lessons => {
        this.lessons = lessons;

        // создаём уникальный список пациентов из уроков
        const patientsMap = new Map<number, any>();
        lessons.forEach(lesson => {
          lesson.patients?.forEach((p: any) => {
            if (!patientsMap.has(p.id)) {
              patientsMap.set(p.id, p);
            }
          });
        });
        this.patients = Array.from(patientsMap.values());

        // выбираем первого пациента по умолчанию
        if (this.patients.length > 0 && !this.selectedPatientId) {
          this.selectedPatientId = this.patients[0].id;
        }

        this.filterLessons();
      });
      this.updateChart();
    }
    setActiveTab(tab: 'account' | 'statistics' | 'help') {
      this.activeTab = tab;
      if (tab === 'statistics' && this.data) {
        this.lessonStore.refresh(this.data.id, 'user');
        this.patientStore.refresh(this.data.id.toString(), 'logoped');
      }
    }

    onPatientSelect(value: string) {
      this.selectedPatientId = Number(value);
      this.updateChart();
    }

    filterLessons() {
      if (this.selectedPatientId && this.lessons.length > 0) {
        this.filteredLessons = this.lessons.filter(lesson =>
          lesson.patients?.some((p: any) => p.id === this.selectedPatientId)
        );
      } else {
        this.filteredLessons = [];
      }
    }

    updateChart() {
      if (!this.selectedPatientId) return;

      const patientLessons = this.lessons.filter(lesson =>
        lesson.patients?.some((p: any) => p.id === this.selectedPatientId)
      );

      // считаем количество по статусам
      const statusCount: Record<LessonStatus, number> = {} as any;
      Object.values(LessonStatus).forEach(status => (statusCount[status] = 0));

      patientLessons.forEach(lesson => {
        const status = lesson.status as LessonStatus; // приведение типа
        if (status in statusCount) {
          statusCount[status] += 1;
        }
      });


      const total = patientLessons.length;
      const labels: string[] = [];
      const data: number[] = [];

      Object.entries(statusCount).forEach(([status, count]) => {
        if (count > 0) {
          labels.push(`${LessonStatusLabels[status as LessonStatus]} (${((count / total) * 100).toFixed(1)}%)`);
          data.push(count);
        }
      });

      this.pieChartData = {
        labels,
        datasets: [{ data }]
      };
    }

    saveChanges() {
      if (!this.data) return;
      this.userDataStore.update(this.data!).subscribe({
        next: () => alert('Данные профиля сохранены!'),
        error: () => alert('Ошибка при сохранении. Попробуйте позже.')
      });
    }


    changeLogoped() {
      alert('Форма смены логопеда откроется здесь');
    }

    contactSupport() {
      alert('Окно поддержки откроется здесь');
    }
  }
