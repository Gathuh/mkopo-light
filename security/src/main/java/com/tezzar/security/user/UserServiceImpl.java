package com.tezzar.security.user;

import com.tezzar.security.exception.DuplicateResourceException;
import com.tezzar.security.exception.ResourceNotFoundException;
import com.tezzar.security.user.enums.UserStatus;
import com.tezzar.security.user.request.CreateUserRequest;
import com.tezzar.security.user.request.UpdateUserRequest;
import com.tezzar.security.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User", "email", request.email());
        }
        return UserResponse.fromEntity(userRepository.save(CreateUserRequest.toUserEntity(request)));
    }

    @Override
    public UserResponse findById(String id) {
        return UserResponse.fromEntity(getUserOrThrow(id));
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findByStatusNot(UserStatus.DELETED)
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        UserEntity user = getUserOrThrow(id);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setRole(request.role());
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public void softDelete(String id) {
        UserEntity user = getUserOrThrow(id);
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    private UserEntity getUserOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
