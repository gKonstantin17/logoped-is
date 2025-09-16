package logopedis.libentities.rsmain.dto.logoped;

import logopedis.libentities.rsmain.dto.user.BaseUserDto;

import java.util.UUID;

public record LogopedDto (
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone
) implements BaseUserDto {}

