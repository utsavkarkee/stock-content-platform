package com.bunnywise.authservice.service;

import com.bunnywise.authservice.dto.AuthRequest;
import com.bunnywise.authservice.dto.AuthResponse;
import com.bunnywise.authservice.dto.RegisterRequest;
import com.bunnywise.authservice.model.User;
import com.bunnywise.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }

    public AuthResponse login(AuthRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent() && passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return new AuthResponse(jwtService.generateToken(request.getEmail()));
        }
        throw new RuntimeException("Invalid credentials");
    }
}
