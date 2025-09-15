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
  import {RouterLink} from '@angular/router';

  @Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, FormsModule, NgChartsModule, RouterLink],
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
        // if (this.patients.length > 0 && !this.selectedPatientId) {
        //   this.selectedPatientId = this.patients[0].id;
        // }

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
      this.filterLessons();  // обновляем список занятий
      this.updateChart();    // обновляем диаграмму
    }

    filterLessons() {
      if (this.selectedPatientId != null && this.lessons.length > 0) {
        this.filteredLessons = this.lessons
          .filter(lesson =>
            lesson.patients?.some((p: any) => p.id === this.selectedPatientId)
          )
          .sort((a, b) => {
            // Сортируем по дате по убыванию
            return new Date(b.dateOfLesson).getTime() - new Date(a.dateOfLesson).getTime();
          });
      } else {
        this.filteredLessons = [];
      }
    }

    updateChart() {
      if (!this.selectedPatientId) {
        // сбрасываем диаграмму если пациент не выбран
        this.pieChartData = { labels: [], datasets: [{ data: [] }] };
        return;
      }

      const patientLessons = this.lessons.filter(lesson =>
        lesson.patients?.some((p: any) => p.id === this.selectedPatientId)
      );

      // считаем количество по нужным группам
      let completed = 0;
      let noShow = 0;
      let canceled = 0;

      patientLessons.forEach(lesson => {
        const status = lesson.status as LessonStatus;

        switch (status) {
          case LessonStatus.COMPLETED:
            completed++;
            break;

          case LessonStatus.NO_SHOW_CLIENT:
            noShow++;
            break;

          case LessonStatus.CANCELED_BY_CLIENT:
          case LessonStatus.CANCELED_BY_LOGOPED:
          case LessonStatus.NO_SHOW_LOGOPED:
            canceled++;
            break;

          default:
            break; // остальные статусы не учитываем
        }
      });

      this.pieChartData = {
        labels: ['Проведено', 'Не состоялось', 'Отменено'],
        datasets: [{ data: [completed, noShow, canceled] }]
      };
    }

    saveChanges() {
      if (!this.data) return;
      this.userDataStore.update(this.data!).subscribe({
        next: () => alert('Данные профиля сохранены!'),
        error: () => alert('Ошибка при сохранении. Попробуйте позже.')
      });
    }
    subTab: 'lessons' | 'speechCards' = 'lessons';
    setSubTab(tab: 'lessons' | 'speechCards') {
      this.subTab = tab;
    }

    getStatusLabel(status: LessonStatus): string {
      return LessonStatusLabels[status];
    }

    changeLogoped() {
      alert('Форма смены логопеда откроется здесь');
    }

    contactSupport() {
      alert('Окно поддержки откроется здесь');
    }
  }
