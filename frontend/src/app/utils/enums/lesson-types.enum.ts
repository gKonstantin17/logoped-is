export enum LessonTypesEnum {
  SOUND_CORRECTION = 'SOUND_CORRECTION',
  LEXICAL_GRAMMATICAL_CORRECTION = 'LEXICAL_GRAMMATICAL_CORRECTION',
  FORMATION_COHERENT_SPEECH = 'FORMATION_COHERENT_SPEECH',
  PHONTETIC_PHONEMIC = 'PHONTETIC_PHONEMIC',
  LOGOPEG_MASSAGE = 'LOGOPEG_MASSAGE',
  DIAGNOSTIC = 'DIAGNOSTIC',
}

export const LessonTypesEnumLabels: Record<LessonTypesEnum, string> = {
  [LessonTypesEnum.SOUND_CORRECTION]: 'Коррекция звукопроизношения"',
  [LessonTypesEnum.LEXICAL_GRAMMATICAL_CORRECTION]: 'Коррекция лексико-грамматических нарушений',
  [LessonTypesEnum.FORMATION_COHERENT_SPEECH]: 'Формирование связной речи',
  [LessonTypesEnum.PHONTETIC_PHONEMIC]: 'Фонетико-фонематические нарушения',
  [LessonTypesEnum.LOGOPEG_MASSAGE]: 'Логопедический массаж',
  [LessonTypesEnum.DIAGNOSTIC]: 'Диагностика',
};
