package logopedis.libentities.rsmain.dto.speechCard;

import java.sql.Timestamp;
import java.util.List;

public record PatientHistoryDto(
        Timestamp date,
        List<String> speechErrors,
        List<String> soundCorrections
) {
}
