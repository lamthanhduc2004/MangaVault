package com.daniel.mangavault.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "username must not be blank")
    @Size(min = 3, max = 50, message = "username must be 3-50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "username may only contain letters, digits and underscores")
    private String username;

    @NotBlank(message = "email must not be blank")
    @Email(message = "email must be valid")
    @Size(max = 255, message = "email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "password must not be blank")
    @Size(min = 6, max = 100, message = "password must be 6-100 characters")
    private String password;
}
