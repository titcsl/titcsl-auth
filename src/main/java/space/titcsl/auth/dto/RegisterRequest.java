package space.titcsl.auth.dto;

import lombok.Data;
import space.titcsl.auth.entity.Role;

@Data
public class RegisterRequest {
    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String displayName;

    private String password;
}
