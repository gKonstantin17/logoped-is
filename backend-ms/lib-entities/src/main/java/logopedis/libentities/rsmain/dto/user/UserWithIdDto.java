package logopedis.libentities.rsmain.dto.user;

import logopedis.libentities.rsmain.dto.user.BaseUserDto;
import java.util.UUID;

public record UserWithIdDto  (
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String role
) implements BaseUserDto {}
