package com.agricontract.auth.service;

import com.agricontract.auth.dto.AuthResponse;
import com.agricontract.auth.dto.LoginRequest;
import com.agricontract.auth.dto.RegisterRequest;
import com.agricontract.auth.entity.User;
import com.agricontract.auth.entity.UserProfile;
import com.agricontract.auth.mapper.UserMapper;
import com.agricontract.auth.repository.UserRepository;
import com.agricontract.common.exception.AppException;
import com.agricontract.common.util.EmailService;
import com.agricontract.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        UserProfile profile = userMapper.toProfileEntity(request);
        profile.setUser(user);
        user.setProfile(profile);

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser);

        try {
            emailService.sendRegistrationEmail(savedUser.getEmail(), profile.getName());
        } catch (Exception e) {
            logger.error("Failed to send registration email to {}", savedUser.getEmail(), e);
        }

        return AuthResponse.builder()
                .token(token)
                .user(userMapper.toDto(savedUser))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        String token = jwtUtil.generateToken(user);

        try {
            emailService.sendLoginEmail(user.getEmail(), user.getProfile().getName());
        } catch (Exception e) {
            logger.error("Failed to send login email to {}", user.getEmail(), e);
        }

        return AuthResponse.builder()
                .token(token)
                .user(userMapper.toDto(user))
                .build();
    }
}
