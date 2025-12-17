package alivium.service;

import alivium.model.dto.request.UpdateUserRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.UserResponse;
import alivium.model.enums.UserRole;
import alivium.model.enums.UserStatus;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    List<UserResponse> getUsersByStatus(UserStatus status);

    List<UserResponse> getUsersByRole(UserRole role);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    MessageResponse updatePassword(Long id, String oldPassword, String newPassword);

    UserResponse updateUserRole(Long userId, UserRole role);

    UserResponse updateUserStatus(Long userId, UserStatus status);

    MessageResponse deleteUser(Long userId);
}
