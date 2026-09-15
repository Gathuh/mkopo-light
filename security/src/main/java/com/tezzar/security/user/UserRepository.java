package com.tezzar.security.user;

import com.tezzar.security.user.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    List<UserEntity> findByStatusNot(UserStatus status);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
