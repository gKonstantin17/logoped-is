export enum LessonStatus {
  PLANNED = 'PLANNED',
  STARTING_SOON = 'STARTING_SOON',
  CANCELED_BY_CLIENT = 'CANCELED_BY_CLIENT',
  CANCELED_BY_LOGOPED = 'CANCELED_BY_LOGOPED',
  NO_SHOW = 'NO_SHOW',
  COMPLETED = 'COMPLETED'
}

export const LessonStatusLabels: Record<LessonStatus, string> = {
  [LessonStatus.PLANNED]: 'Запланировано',
  [LessonStatus.STARTING_SOON]: 'Скоро начнётся',
  [LessonStatus.CANCELED_BY_CLIENT]: 'Отменено клиентом',
  [LessonStatus.CANCELED_BY_LOGOPED]: 'Отменено логопедом',
  [LessonStatus.NO_SHOW]: 'Не состоялось (клиент не пришёл)',
  [LessonStatus.COMPLETED]: 'Проведено'
};
// use
// HTML:
//  Статус занятия: {{ LessonStatusLabels[lesson.status] }}

// ts:
// lesson = {
//     status: LessonStatus.PLANNED
//   };
