package logopedis.libentities.enums;

public enum LessonStatus {
    PLANNED("Запланировано"),
    STARTING_SOON("Скоро начнётся"),
    CANCELED_BY_CLIENT("Отменено клиентом"),
    CANCELED_BY_LOGOPED("Отменено логопедом"),
    NO_SHOW("Не состоялось (клиент не пришёл)"),
    COMPLETED("Проведено");

    private final String description;

    LessonStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

