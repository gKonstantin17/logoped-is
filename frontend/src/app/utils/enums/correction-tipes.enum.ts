export enum CorrectionTypesEnum {
  PLACED = 'PLACED',           // Поставлен
  INTRODUCED = 'INTRODUCED',   // Введен в речь
  AUTOMATED = 'AUTOMATED',     // Автоматизирован
  DIFFERENTIATED = 'DIFFERENTIATED', // Дифференцирован
  NORMALIZED = 'NORMALIZED'    // Звукопроизношение в норме
}

// Маппинг на читаемые названия для оси Y и tooltip
export const CorrectionTypesLabels: Record<CorrectionTypesEnum, string> = {
  [CorrectionTypesEnum.PLACED]: 'Поставлен',
  [CorrectionTypesEnum.INTRODUCED]: 'Введен в речь',
  [CorrectionTypesEnum.AUTOMATED]: 'Автоматизирован',
  [CorrectionTypesEnum.DIFFERENTIATED]: 'Дифференцирован',
  [CorrectionTypesEnum.NORMALIZED]: 'Звукопроизношение в норме'
};

// Массив категорий для Chart.js (ось Y)
export const correctionTypes: string[] = [
  'Поставлен',
  'Введен в речь',
  'Автоматизирован',
  'Дифференцирован',
  'Звукопроизношение в норме'
];
export const correctionTypesArray: CorrectionTypesEnum[] = [
  CorrectionTypesEnum.PLACED,
  CorrectionTypesEnum.INTRODUCED,
  CorrectionTypesEnum.AUTOMATED,
  CorrectionTypesEnum.DIFFERENTIATED,
  CorrectionTypesEnum.NORMALIZED
];
export const labelToEnumMap: Record<string, CorrectionTypesEnum> = {
  'Поставлен': CorrectionTypesEnum.PLACED,
  'Введен в речь': CorrectionTypesEnum.INTRODUCED,
  'Автоматизирован': CorrectionTypesEnum.AUTOMATED,
  'Дифференцирован': CorrectionTypesEnum.DIFFERENTIATED,
  'Звукопроизношение в норме': CorrectionTypesEnum.NORMALIZED
};
