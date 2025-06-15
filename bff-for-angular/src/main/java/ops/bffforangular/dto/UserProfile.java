package ops.bffforangular.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfile {
    private String given_name;
    private String family_name;
    private String email;
    private String phone;
    private String role;
    private String id;
}
