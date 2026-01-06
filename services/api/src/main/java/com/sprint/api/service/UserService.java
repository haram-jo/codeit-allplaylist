package com.sprint.api.service;


import com.sprint.api.dto.ChangePasswordRequest;
import com.sprint.api.dto.UserCreateRequest;
import com.sprint.api.dto.UserDto;
import com.sprint.api.dto.UserLockUpdateRequest;
import com.sprint.api.dto.UserRoleUpdateRequest;
import com.sprint.api.dto.UserUpdateRequest;
import com.sprint.api.entity.User;
import com.sprint.api.entity.UserRole;
import com.sprint.api.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // 필수 필드 생성자 자동 생성
public class UserService {

  private final UserRepository userRepository;

  /* ✅ 회원가입
  * -record DTO는 request.getEmail() X -> request.email() O로 꺼냄
  * -저장된 엔티티를 UserDto로 변환하여 반환
  * */
  @Transactional
  public UserDto createUser(UserCreateRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

      //엔티티 생성
    User user = User.builder()
        .name(request.name())
        .email(request.email())
        .password(request.password())
        .build();

    User saved = userRepository.save(user);

    //엔티티 DTO로 변환해서 반환
    return new UserDto(
        saved.getId(),
        saved.getCreatedAt(),
        saved.getEmail(),
        saved.getName(),
        saved.getProfileImageUrl(),
        saved.getRole(),
        saved.isLocked()
    );
    }

    /* ✅ 사용자 단건 조회(상세 조회)
    */
    @Transactional(readOnly = true)
    public UserDto getUser(String userId) {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. id=" + userId));

      return new UserDto(
          user.getId(),
          user.getCreatedAt(),
          user.getEmail(),
          user.getName(),
          user.getProfileImageUrl(),
          user.getRole(),
          user.isLocked()
      );
    }

    /* ✅ 사용자 전체 조회 (목록 조회)
    * */
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
            .map(user -> new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getEmail(),
                user.getName(),
                user.getProfileImageUrl(),
                user.getRole(),
                user.isLocked()
            ))
            .collect(Collectors.toList());
    }


  /* ✅ 사용자 프로필 업데이트
   * */
  @Transactional
  public UserDto updateProfile(String userId, UserUpdateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. id=" + userId));

    if (request.name() != null) user.changeName(request.name());

    User saved = userRepository.save(user);
    return new UserDto(
        saved.getId(),
        saved.getCreatedAt(),
        saved.getEmail(),
        saved.getName(),
        saved.getProfileImageUrl(),
        saved.getRole(),
        saved.isLocked()
    );
  }

  /* ✅ 사용자 권한 수정 (Admin 전용)
   * */
  @Transactional
  public UserDto updateRole(String userId, UserRoleUpdateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. id=" + userId));

    if (request.role() != null) user.changeRole(request.role());

    User saved = userRepository.save(user);
    return new UserDto(
        saved.getId(),
        saved.getCreatedAt(),
        saved.getEmail(),
        saved.getName(),
        saved.getProfileImageUrl(),
        saved.getRole(),
        saved.isLocked()
    );
  }

  /* ✅ 사용자 비밀번호 수정
   * */
  @Transactional
  public void updatePassword(String userId, ChangePasswordRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. id=" + userId));

    // ✅ 지금은 최소 구현: 일단 그대로 저장 (나중에 PasswordEncoder 붙이기)
    user.changePassword(request.newPassword());
    userRepository.save(user);
  }

  /* ✅ 사용자 계정 잠금/잠금 해제 (Admin 전용)
   * */
  @Transactional
  public UserDto updateLocked(String userId, UserLockUpdateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. id=" + userId));

    if (request.locked() != null) user.changeLocked(request.locked());

    User saved = userRepository.save(user);
    return new UserDto(
        saved.getId(),
        saved.getCreatedAt(),
        saved.getEmail(),
        saved.getName(),
        saved.getProfileImageUrl(),
        saved.getRole(),
        saved.isLocked()
    );
  }
}

