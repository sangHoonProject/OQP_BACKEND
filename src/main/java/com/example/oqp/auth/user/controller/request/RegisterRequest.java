package com.example.oqp.auth.user.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email(message = "이메일 형식이 잘못되었습니다.")
    @NotBlank
    private String email;

    @NotBlank(message = "비밀번호는 필수값입니다.")
    private String password;

    @NotBlank(message = "이름은 필수값입니다.")
    private String name;
}
