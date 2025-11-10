package com.deliverit.user.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserEditRequestDto {

    private String name;

    private String phone;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/~`|\\\\]).{8,15}$",
            message = "비밀번호는 8~15자이며, 알파벳 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String newPassword;

    private String currentPassword;
}
