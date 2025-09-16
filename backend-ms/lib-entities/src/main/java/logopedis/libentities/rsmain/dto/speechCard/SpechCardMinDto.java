package logopedis.libentities.rsmain.dto.speechCard;

import java.sql.Timestamp;
import java.util.List;

public record SpechCardMinDto(
        Long patientId,
        String patientName,
        Timestamp diagnosticDate,
        List<String>speechErrors,
        List<String> soundCorrections,
        Long speechCardId
) {
}
