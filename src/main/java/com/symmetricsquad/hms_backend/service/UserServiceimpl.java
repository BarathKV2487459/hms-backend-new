package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.model.ContactQuery;
import com.symmetricsquad.hms_backend.model.User;
import com.symmetricsquad.hms_backend.dto.request.ChangePasswordRequest;
import com.symmetricsquad.hms_backend.dto.request.UserRequest;
import com.symmetricsquad.hms_backend.dto.response.UserResponse;
import com.symmetricsquad.hms_backend.model.enums.UserRole;
import com.symmetricsquad.hms_backend.repository.ContactQueryRepository;
import com.symmetricsquad.hms_backend.repository.PatientProfileRepository;
import com.symmetricsquad.hms_backend.repository.UserRepository;
import com.symmetricsquad.hms_backend.dto.request.ContactQueryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final ContactQueryRepository contactQueryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse registerUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setRole(UserRole.PATIENT);

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Override
    public UserResponse loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No account found for email: " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        return toResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = findUserById(id);

        if (request.getFullName()  != null) user.setFullName(request.getFullName());
        if (request.getAddress()   != null) user.setAddress(request.getAddress());
        if (request.getGender()    != null) user.setGender(request.getGender());

        // Email change — check uniqueness first
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already in use: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public String deleteUser(Long id) {
        User user = findUserById(id);
        user.softDelete();

        // Soft-delete linked profile in the same transaction
        if (user.getRole() == UserRole.PATIENT && user.getPatientProfile() != null) {
            user.getPatientProfile().softDelete();
            patientProfileRepository.save(user.getPatientProfile());
        }

        userRepository.save(user);
        return "User deleted successfully";
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        User user = findUserById(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public String saveContactQuery(ContactQueryRequest request) {
        ContactQuery query = new ContactQuery();
        query.setFullName(request.getFullName());
        query.setEmail(request.getEmail());
        query.setContactNo(request.getContactNo());
        query.setMessage(request.getMessage());
        contactQueryRepository.save(query);
        return "Query submitted successfully";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    private UserResponse toResponse(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setRole(user.getRole());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }
}
