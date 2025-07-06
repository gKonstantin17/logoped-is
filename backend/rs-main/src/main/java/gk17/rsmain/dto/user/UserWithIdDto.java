package gk17.rsmain.dto.user;

import java.util.UUID;

public record UserWithIdDto  (
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String role
) implements BaseUserDto {}
