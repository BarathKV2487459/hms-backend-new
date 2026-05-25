package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.dto.request.ChangePasswordRequest;
import com.symmetricsquad.hms_backend.dto.request.ContactQueryRequest;
import com.symmetricsquad.hms_backend.dto.request.UserRequest;
import com.symmetricsquad.hms_backend.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    /** Register a new user (role = PATIENT by default) */
    UserResponse registerUser(UserRequest request);

    /** Authenticate and return user details (JWT generation handled in controller/security layer) */
    UserResponse loginUser(String email, String password);

    /** Get all users — paginated */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /** Get a single user by ID */
    UserResponse getUserById(Long id);

    /** Update user profile fields (name, address, phone, etc.) */
    UserResponse updateUser(Long id, UserRequest request);

    /** Soft-delete user + their linked PatientProfile or DoctorProfile */
    String deleteUser(Long id);

    /** Change password with old-password verification */
    void changePassword(Long userId, ChangePasswordRequest request);

    /** Submit a contact/support query (public endpoint, no auth needed) */
    String saveContactQuery(ContactQueryRequest request);
}