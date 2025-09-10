package logopedis.libentities.enums;

public enum DiagnosticTypes {
    BEGIN("Начальная"), // при первом создании речевой карты
    AFTER_LESSON("После занятия"), // за 60 минут до начала
    MIDDLE("Промежуточная"), // Повторное обследование
    FINISH("Итоговая");

    private final String description;

    DiagnosticTypes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}