package logopedis.rsmain.dto.user;

import logopedis.rsmain.dto.user.BaseUserDto;

public record UserDto (
        String firstName,
        String lastName,
        String email,
        String phone
) implements BaseUserDto {}
