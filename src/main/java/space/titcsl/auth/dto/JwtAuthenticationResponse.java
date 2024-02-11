package space.titcsl.auth.dto;

import lombok.Data;

@Data
public class JwtAuthenticationResponse {

    private String access_token;
    private String refresh_token;
}
