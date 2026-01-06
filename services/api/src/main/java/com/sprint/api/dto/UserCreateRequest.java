package com.sprint.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest (

  @NotBlank(message = "이름은 필수 입력 값입니다.")
  String name,

  @NotBlank(message = "이메일은 필수 입력 값입니다.")
  @Email(message = "유효한 이메일을 입력해주세요")
  String email,

  @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
  @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
  String password
) {}
