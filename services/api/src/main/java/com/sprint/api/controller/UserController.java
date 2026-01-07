package com.sprint.api.controller;

import com.sprint.api.dto.user.ChangePasswordRequest;
import com.sprint.api.dto.user.UserCreateRequest;
import com.sprint.api.dto.user.UserDto;
import com.sprint.api.dto.user.UserLockUpdateRequest;
import com.sprint.api.dto.user.UserRoleUpdateRequest;
import com.sprint.api.dto.user.UserUpdateRequest;
import com.sprint.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor //필수 필드 생성자 자동 생성
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  // 회원가입
  @PostMapping
  public ResponseEntity<UserDto> createUser(@Validated @RequestBody UserCreateRequest request) {
    UserDto createdUser = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  //상세 조회
  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getUser(@PathVariable String userId) {
    return ResponseEntity.ok(userService.getUser(userId));
  }

  //목록 조회 - 커서는 추후 추가 예정
  @GetMapping
  public ResponseEntity<List<UserDto>> getUsers() {
    return ResponseEntity.ok(userService.getUsers());
  }

  //프로필 이름 변경
  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> updateProfile(
      @PathVariable String userId,
      @RequestBody UserUpdateRequest request
  ) {
    return ResponseEntity.ok(userService.updateProfile(userId, request));
  }

  //권한 수정
  @PatchMapping("/{userId}/role")
  public ResponseEntity<UserDto> updateRole(
      @PathVariable String userId,
      @RequestBody UserRoleUpdateRequest request
  ) {
    return ResponseEntity.ok(userService.updateRole(userId, request));
  }

  //비밀번호 변경
  @PatchMapping("/{userId}/password")
  public ResponseEntity<Void> updatePassword(
      @PathVariable String userId,
      @RequestBody ChangePasswordRequest request
  ) {
    userService.updatePassword(userId, request);
    return ResponseEntity.noContent().build();
  }

  //잠금 상태 변경
  @PatchMapping("/{userId}/locked")
  public ResponseEntity<UserDto> updateLocked(
      @PathVariable String userId,
      @RequestBody UserLockUpdateRequest request
  ) {
    return ResponseEntity.ok(userService.updateLocked(userId, request));
  }
}


