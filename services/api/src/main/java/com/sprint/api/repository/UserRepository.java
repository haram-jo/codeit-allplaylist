package com.sprint.api.repository;

import com.sprint.api.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

  //이메일로 중복 가입 여부 확인
  boolean existsByEmail(String email);

  //이메일로 사용자 찾기 (로그인에서 필요)
  Optional<User> findByEmail(String email); //email을 받아서 UserEntity 반환



}
