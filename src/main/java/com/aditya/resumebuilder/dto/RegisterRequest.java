package com.aditya.resumebuilder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @Email(message = "email is required and should be valid")
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "name is required")
    @Size(min = 3, max = 15, message = "name must be between 3 and 15 characters")
    private String name;
    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be of at least 6 characters")
    private String password;
    private String profileImageUrl;
}
