package alivium.service;

import alivium.domain.entity.User;
import alivium.domain.repository.UserRepository;
import alivium.exception.AlreadyExistsException;
import alivium.exception.BusinessException;
import alivium.exception.NotFoundException;
import alivium.mapper.UserMapper;
import alivium.model.dto.request.UpdateUserRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.UserResponse;
import alivium.model.enums.UserRole;
import alivium.model.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Cacheable(value = "users", key = "'all'")
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toListResponse(users);
    }

    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findById(id);
        return userMapper.toResponse(user);
    }

    @Cacheable(value = "users", key = "'email:' + #email")
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Cacheable(value = "users", key = "'status:' + #status")
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByStatus(UserStatus status) {
        List<User> users = userRepository.findByStatus(status);
        return userMapper.toListResponse(users);
    }

    @Cacheable(value = "users", key = "'role:' + #role")
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(UserRole role) {
        List<User> users = userRepository.findByRole(role);
        return userMapper.toListResponse(users);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (request.getPhoneNumber() != null &&
                !request.getPhoneNumber().isBlank() &&
                !request.getPhoneNumber().equals(user.getPhoneNumber())) {

            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new AlreadyExistsException("Phone number already exists: " + request.getPhoneNumber());
            }
        }

        userMapper.updateUserFromRequest(user, request);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public MessageResponse updatePassword(Long id, String oldPassword, String newPassword) {
        User user = findById(id);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("Old password does not match");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new MessageResponse("Password updated successfully");
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserResponse updateUserRole(Long userId, UserRole role) {
        User user = findById(userId);
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserResponse updateUserStatus(Long userId, UserStatus status) {
        User user = findById(userId);
        user.setStatus(status);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public MessageResponse deleteUser(Long userId) {
        User user = findById(userId);
        userRepository.delete(user);
        return new MessageResponse("User deleted successfully");
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }
}