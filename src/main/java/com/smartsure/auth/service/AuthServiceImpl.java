package com.smartsure.auth.service;

import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartsure.auth.dto.ApiResponse;
import com.smartsure.auth.dto.LoginRequest;
import com.smartsure.auth.dto.RegisterRequest;
import com.smartsure.auth.dto.UserResponse;
import com.smartsure.auth.entity.Role;
import com.smartsure.auth.entity.RoleName;
import com.smartsure.auth.entity.User;
import com.smartsure.auth.exception.AuthServiceException;
import com.smartsure.auth.mapper.UserMapper;
import com.smartsure.auth.repository.RoleRepository;
import com.smartsure.auth.repository.UserRepository;
import com.smartsure.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public ApiResponse<UserResponse> register(RegisterRequest request) {
        log.info("Processing registration for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Registration failed: Email already exists - {}", request.getEmail());
            return ApiResponse.error("Email is already registered");
        }

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new AuthServiceException("Role not found"));

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword())); // ✅ encoded
        user.setRoles(Set.of(customerRole)); // ✅ set BEFORE save

        User savedUser = userRepository.save(user);

        log.info("Successfully registered user: {}", request.getEmail());
        return ApiResponse.success("User registered successfully", userMapper.toResponse(savedUser));
    }

    @Override
    public ApiResponse<UserResponse> login(LoginRequest request) {
        log.info("Processing login for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthServiceException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) { // ✅ proper check
            log.error("Login failed: Invalid password for {}", request.getEmail());
            throw new AuthServiceException("Invalid credentials");
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();
        String token = jwtUtil.generateToken(user.getEmail(), roles);

        UserResponse userResponse = userMapper.toResponse(user);
        userResponse.setToken(token);

        log.info("Successfully logged in user: {}", request.getEmail());
        return ApiResponse.success("Login successful", userResponse);
    }
}