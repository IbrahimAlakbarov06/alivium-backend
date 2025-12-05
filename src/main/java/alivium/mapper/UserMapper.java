package alivium.mapper;

import alivium.domain.entity.User;
import alivium.model.dto.request.RegisterRequest;
import alivium.model.dto.request.UpdateUserRequest;
import alivium.model.dto.response.UserResponse;
import alivium.model.enums.AuthProvider;
import alivium.model.enums.UserRole;
import alivium.model.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profileImage(user.getProfileImage())
                .status(user.getStatus())
                .role(user.getRole())
                .authProvider(user.getAuthProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toEntity(RegisterRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .authProvider(AuthProvider.LOCAL)
                .role(UserRole.USER_ROLE)
                .status(UserStatus.UNVERIFIED)
                .active(true)
                .build();
    }

    public User toGoogleUser(String email, String firstName, String lastName, String profileImage, String encodedPassword) {
        return User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .profileImage(profileImage)
                .password(encodedPassword)
                .authProvider(AuthProvider.GOOGLE)
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER_ROLE)
                .active(true)
                .build();
    }

    public void updateUserFromRequest(User user, UpdateUserRequest request) {
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }
    }

    public List<UserResponse> toListResponse(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}