package logopedis.libentities.enums;

public enum NotificationMsg {
    PLANNED("Вы записаны на занятие"), // при создании занятия
    STARTING_SOON_15М("Занятие начнется через 15 минут"), // За 15 минут до начала
    STARTING_SOON_1H("Занятие начнется через час"), // За 15 минут до начала
    IN_PROGRESS("Занятие началось"), // Началось
    CANCELED_BY_CLIENT("Занятие отменено клиентом"), // Отмена до начала занятия
    CANCELED_BY_LOGOPED("Занятие отменено логопедом"), // Отмена до начала занятия
    NO_SHOW_CLIENT("Пациент отсутствовал на занятии"), // Не началось после 15 мин от начала занятия
    NO_SHOW_LOGOPED("Логопед отсутствовал на занятии"), // Не началось после 15 мин от начала занятия
    COMPLETED("Занятие прошло"); // По окончанию занятия (через час после начала)

    private final String description;

    NotificationMsg(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
