package logopedis.libentities.rsmain.dto.logoped;

import logopedis.rsmain.dto.user.BaseUserDto;

public record LogopedDto (
        String firstName,
        String lastName,
        String email,
        String phone
) implements BaseUserDto {}

