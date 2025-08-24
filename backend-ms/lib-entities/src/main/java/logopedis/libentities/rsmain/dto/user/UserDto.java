package logopedis.libentities.rsmain.dto.user;

public record UserDto (
        String firstName,
        String lastName,
        String email,
        String phone
) implements BaseUserDto {}
