export enum LessonStatus {
  PLANNED = 'PLANNED',
  PLANNED_1H = 'PLANNED_1H',
  STARTING_SOON = 'STARTING_SOON',
  IN_PROGRESS = 'IN_PROGRESS',
  CANCELED_BY_CLIENT = 'CANCELED_BY_CLIENT',
  CANCELED_BY_LOGOPED = 'CANCELED_BY_LOGOPED',
  NO_SHOW_CLIENT = 'NO_SHOW_CLIENT',
  NO_SHOW_LOGOPED = 'NO_SHOW_LOGOPED',
  COMPLETED = 'COMPLETED'
}

export const LessonStatusLabels: Record<LessonStatus, string> = {
  [LessonStatus.PLANNED]: 'Запланировано',
  [LessonStatus.PLANNED_1H]: 'Скоро начнётся',
  [LessonStatus.STARTING_SOON]: 'Начнется вот-вот',
  [LessonStatus.IN_PROGRESS]: 'В процессе',
  [LessonStatus.CANCELED_BY_CLIENT]: 'Отменено клиентом',
  [LessonStatus.CANCELED_BY_LOGOPED]: 'Отменено логопедом',
  [LessonStatus.NO_SHOW_CLIENT]: 'Не состоялось (клиент не пришёл)',
  [LessonStatus.NO_SHOW_LOGOPED]: 'Не состоялось (логопед отсутствовал)',
  [LessonStatus.COMPLETED]: 'Проведено'
};
// use
// HTML:
//  Статус занятия: {{ LessonStatusLabels[lesson.status] }}

// ts:
// lesson = {
//     status: LessonStatus.PLANNED
//   };
