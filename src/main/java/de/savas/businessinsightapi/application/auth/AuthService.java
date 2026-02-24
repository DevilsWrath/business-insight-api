package de.savas.businessinsightapi.application.auth;

import de.savas.businessinsightapi.common.error.BadRequestException;
import de.savas.businessinsightapi.common.error.UnauthorizedException;
import de.savas.businessinsightapi.domain.user.User;
import de.savas.businessinsightapi.domain.user.UserRepository;
import de.savas.businessinsightapi.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(String email, String username, String password) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        String hashed = passwordEncoder.encode(password);
        User user = new User(email, username, hashed);
        userRepository.save(user);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername((username))
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwtService.generateAccessToken(user);
    }
}