package com.sprint.api.entity.user;

import com.sprint.api.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* @Data 지양
* 무분별한 @Setter로 인해 엔티티값 변경을 방지기 위해 사용하지 않음
* */

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Id
  @Column(length = 36)
  private String id = UUID.randomUUID().toString();

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  private UserRole role = UserRole.USER; //기본값 USER

  @Column(name = "locked")
  private boolean locked = false; //기본값 false

  @Column(name = "profile_image_url")
  private String profileImageUrl; //프로필 이미지 URL

  @Column(name = "auth_provider")
  private String authProvider; //인증 제공자 (예: GOOGLE, FACEBOOK)

  @Column(name = "provider_user_id")
  private String providerUserId; //제공한 사용자 ID

  @Builder // 실제 값 넣은 필드만 토대로 객체가 생성됨, 순서 상관X
  public User(String name, String email, String password, UserRole role, Boolean locked,
      String profileImageUrl, String authProvider, String providerUserId) {

    this.name = name;
    this.email = email;
    this.password = password;
    this.role = (role != null) ? role : UserRole.USER; //role 값 null이면 USER로
    this.locked = (locked != null) ? locked : false; //locked 값 null이면 false로
    this.profileImageUrl = profileImageUrl;
    this.authProvider = authProvider;
    this.providerUserId = providerUserId;
  }

  //유저 이름 변경
  public void changeName(String name) {
    this.name = name;
  }

  //유저 권한 변경
  public void changeRole(UserRole role) {
    this.role = role;
  }

  //유저 비밀번호 변경
  public void changePassword(String spassword) {
    this.password = password;
  }

  //유저 잠금 상태 변경
  public void changeLocked(boolean locked) {
    this.locked = locked;
  }
}





