package com.viksingh.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginResponse implements Serializable {
    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("expires_in")
    public long expiresIn;

    @JsonProperty("refresh_expires_in")
    public long refreshExpiresIn;

    @JsonProperty("refresh_token")
    public String refreshToken;

    @JsonProperty("token_type")
    public String tokenType;
    @JsonProperty("not-before-policy")
    public int notBeforePolicy;

    @JsonProperty("session_state")
    public String sessionState;

    public String scope;
}
