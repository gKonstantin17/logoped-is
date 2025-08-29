package logopedis.libentities.enums;

public enum LessonStatus {
    PLANNED("Запланировано"), // при создании занятия
    STARTING_SOON("Скоро начнётся"), // За 15 минут до начала
    IN_PROGRESS("В процессе"), // Началось
    CANCELED_BY_CLIENT("Отменено клиентом"), // Отмена до начала занятия
    CANCELED_BY_LOGOPED("Отменено логопедом"), // Отмена до начала занятия
    NO_SHOW_CLIENT("Не состоялось (клиент не пришёл)"), // Не началось после 15 мин от начала занятия
    NO_SHOW_LOGOPED("Не состоялось (логопед отсутствовал)"), // Не началось после 15 мин от начала занятия
    COMPLETED("Проведено"); // По окончанию занятия (через час после начала)

    private final String description;

    LessonStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

