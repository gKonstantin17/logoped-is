package logopedis.libentities.msnotification.dto.recipient;

import java.util.UUID;

public record RecipientDataDto(
        Long patientId,
        UUID userId
) {
}
