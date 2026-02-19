package de.savas.businessinsightapi.application.auth;

import de.savas.businessinsightapi.domain.user.User;
import de.savas.businessinsightapi.domain.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String email, String password) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        String hashed = passwordEncoder.encode(password);
        User user = new User(email, hashed);
        userRepository.save(user);
    }
}