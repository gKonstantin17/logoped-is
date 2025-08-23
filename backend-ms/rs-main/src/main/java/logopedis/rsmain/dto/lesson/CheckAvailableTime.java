package logopedis.rsmain.dto.lesson;

import java.sql.Timestamp;

// данные чтобы проверить свободное время на дату
public record CheckAvailableTime (
       Long patientId,
       Timestamp date
) { }
