package com.deliverit.user.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    @NotBlank
    @Size(min = 4, max = 10, message = "아이디는 최소 4 최대 10자여야 합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 알파벳(소문자)과 숫자만 입력 가능합니다.")
    private String username;

    @NotBlank
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8 최대 15자여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]+$",
            message = "비밀번호는 알파벳(대소문자), 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    @NotBlank
    private String role = "";

    private String adminToken = "";
}