package logopedis.libentities.rsmain.dto.user;
import logopedis.libentities.rsmain.dto.user.BaseUserDto;
public record UserDto (
        String firstName,
        String lastName,
        String email,
        String phone
) implements BaseUserDto {}
