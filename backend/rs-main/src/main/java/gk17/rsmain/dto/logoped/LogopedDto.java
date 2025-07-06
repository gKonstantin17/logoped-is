package gk17.rsmain.dto.logoped;

import gk17.rsmain.dto.user.BaseUserDto;

public record LogopedDto (
        String firstName,
        String lastName,
        String email,
        String phone
) implements BaseUserDto {}

