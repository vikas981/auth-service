package com.viksingh.authservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
