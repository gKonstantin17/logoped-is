  import {Component, OnInit} from '@angular/core';
  import { CommonModule } from '@angular/common';
  import { FormsModule } from '@angular/forms';
  import {UserData} from '../../utils/services/user-data.service';
  import {UserDataStore} from '../../utils/stores/user-data.store';
  import {LessonStore} from '../../utils/stores/lesson.store';
  import {PatientStore} from '../../utils/stores/patient.store';
  import {Chart, ChartConfiguration, ChartData, ChartOptions, ChartType} from 'chart.js';
  import {LessonStatus, LessonStatusLabels} from '../../utils/enums/lesson-status.enum';
  import {NgChartsModule} from 'ng2-charts';
  import {Router, RouterLink} from '@angular/router';
  import {SpeechCardStore} from '../../utils/stores/speechCard.store';
  import 'chartjs-adapter-date-fns';
  import { addDays } from 'date-fns';
  import { parseISO } from 'date-fns/parseISO';
  import { ru } from 'date-fns/locale';

  import {
  correctionTypesArray,
  CorrectionTypesEnum,
    CorrectionTypesLabels,
    labelToEnumMap
  } from '../../utils/enums/correction-tipes.enum';

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
    currentRole: string | null = null;
    patients: any[] = []; // список пациентов для select
    // Для вкладки "Пациенты" у логопеда
    patientsCards: any[] = []; // данные всех пациентов с первичными картами
    patientsChartData: ChartData<'pie', number[], string | string[]> = { labels: [], datasets: [{ data: [] }] };


    speechCategories: CorrectionTypesEnum[] = correctionTypesArray;
    public lineChartData: ChartData<'line'> = {
      labels: [],
      datasets: []
    };

    public lineChartOptions: ChartOptions<'line'> = {
      responsive: true,
      interaction: {
        mode: 'nearest',
        intersect: false
      },
      plugins: {
        tooltip: {
          callbacks: {
            label: (tooltipItem) => {
              const datasetLabel = tooltipItem.dataset.label || '';
              const pointData = tooltipItem.raw as any;
              const status = pointData.rawStatus || 'Неизвестно';
              const date = new Date(tooltipItem.parsed.x).toLocaleDateString('ru-RU');
              return `${datasetLabel}: ${status} (${date})`;
            }
          }
        }
      },
      scales: {
        x: {
          type: 'time',
          time: {
            unit: 'day',
            tooltipFormat: 'dd.MM.yyyy',
            displayFormats: {
              day: 'dd MMM'
            }
          },
          adapters: {
            date: {
              locale: ru
            }
          },
          title: { display: true, text: 'Дата' }
        },
        y: {
          type: 'category',
          labels: correctionTypesArray.map(s => CorrectionTypesLabels[s]),
          title: {
            display: true,
            text: 'Статус'
          },
          reverse: true
        }
      }
    };
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
      private patientStore: PatientStore,
      private speechCardStore: SpeechCardStore,
      private router: Router
    ) {
      const chart = Chart as any;
      if (chart.defaults?.adapters?.date) {
        chart.defaults.adapters.date.locale = ru;
      }
    }

    ngOnInit() {
      // данные пользователя
      this.userDataStore.userData$.subscribe(user => {
        this.currentRole = user?.role || null;
        console.log('user'+user?.id);
        if (user && this.currentRole === 'user') {
          this.data = user;
          this.lessonStore.refresh(user.id, 'user');
        }

        if (user && this.currentRole === 'logoped') {
          this.data = user;
          this.lessonStore.refresh(user.id, 'logoped');
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

      if (tab === 'statistics' && this.data && this.currentRole) {
        this.lessonStore.refresh(this.data.id, this.currentRole);
        this.patientStore.refresh(this.data.id.toString(), this.currentRole);

        if (this.currentRole === 'logoped') {
          // загружаем первичные карты пациентов
          this.loadPatientsCards();
        }
      }
    }


    onPatientSelect(value: string) {
      this.selectedPatientId = Number(value);
      this.filterLessons();  // обновляем список занятий
      this.updateChart();    // обновляем диаграмму

      // Получаем историю пациента
      if (this.selectedPatientId != null) {
        this.speechCardStore.findPatientHistory(this.selectedPatientId).subscribe({
          next: (history) => {
            console.log('История пациента:', history);
          },
          error: (err) => {
            console.error('Ошибка при получении истории пациента', err);
          }
        });
      }
      if (this.subTab === 'speechCards' && this.selectedPatientId != null) {
        this.loadSpeechChart();
      }
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

    loadSpeechChart() {
      if (!this.selectedPatientId) return;

      this.speechCardStore.findPatientHistory(this.selectedPatientId).subscribe({
        next: (history) => {
          // сортируем по дате
          history.sort((a: any, b: any) => new Date(a.date).getTime() - new Date(b.date).getTime());

          // собираем уникальные звуки (Р, Ж и т.д.)
          const soundSet = new Set<string>();
          history.forEach((h: any) => {
            h.soundCorrections.forEach((sc: string) => {
              const sound = sc.split(':')[0].trim();
              soundSet.add(sound);
            });
          });
          const sounds = Array.from(soundSet);

          // создаем dataset для каждого звука
          const datasets = sounds.map(sound => {
            const dataPoints: any[] = [];

            history.forEach((h: any) => {
              // Ищем запись для текущего звука в этой дате
              const soundRecord = h.soundCorrections.find((sc: string) =>
                sc.startsWith(sound + ':')
              );

              if (soundRecord) {
                const statusLabel = soundRecord.split(':')[1].trim();
                const statusEnum = labelToEnumMap[statusLabel];

                if (statusEnum) {
                  // Используем строковое значение для категориальной оси Y
                  dataPoints.push({
                    x: parseISO(h.date),
                    y: CorrectionTypesLabels[statusEnum], // Используем label для оси Y
                    rawStatus: statusLabel,
                    rawEnum: statusEnum
                  });
                }
              }
            });

            return {
              label: sound,
              data: dataPoints,
              fill: false,
              tension: 0.4,
              pointRadius: 5,
              pointHoverRadius: 7
            };
          });

          this.lineChartData = {
            labels: history.map((h: any) => parseISO(h.date)),
            datasets
          };
        },
        error: (err) => console.error(err)
      });
    }


    loadPatientsCards() {
      if (!this.data) return;

      this.speechCardStore.findFirstAllByPatient(this.data.id.toString()).subscribe({
        next: (cards: any[]) => {
          this.patientsCards = cards;

          // обновляем круговую диаграмму
          const speechErrorsCount: Record<string, number> = {};
          cards.forEach(card => {
            card.speechErrors.forEach((error: string) => {
              speechErrorsCount[error] = (speechErrorsCount[error] || 0) + 1;
            });
          });

          this.patientsChartData = {
            labels: Object.keys(speechErrorsCount),
            datasets: [{
              data: Object.values(speechErrorsCount)
            }]
          };
        },
        error: (err) => console.error('Ошибка при загрузке пациентов', err)
      });
    }



    saveChanges() {
      if (!this.data) return;
      this.userDataStore.update(this.data!).subscribe({
        next: () => alert('Данные профиля сохранены!'),
        error: () => alert('Ошибка при сохранении. Попробуйте позже.')
      });
    }
    subTab: 'lessons' | 'speechCards' | 'patients' = 'lessons';
    setSubTab(tab: 'lessons' | 'speechCards' | 'patients') {
      this.subTab = tab;

      if (tab === 'speechCards' && this.selectedPatientId != null) {
        this.loadSpeechChart();
      }

      if (tab === 'patients' && this.currentRole === 'logoped') {
        this.loadPatientsCards();
      }
    }
    getStatusLabel(status: LessonStatus): string {
      return LessonStatusLabels[status];
    }

    goToSpeechCard(speechCardId: number) {
      if (!speechCardId) {
        alert('Невозможно перейти, идентификатор речевой карты отсутствует');
        return;
      }

      this.router.navigate(['/dashboard/speechcard'], { queryParams: { speechCardId } });
    }

    changeLogoped() {
      alert('Форма смены логопеда откроется здесь');
    }

    contactSupport() {
      alert('Окно поддержки откроется здесь');
    }
  }
