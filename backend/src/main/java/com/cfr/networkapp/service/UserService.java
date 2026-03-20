package com.cfr.networkapp.service;

import com.cfr.networkapp.model.User;
import com.cfr.networkapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User registerUser(String username, String email, String password, String firstName, String lastName) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User(username, email, passwordEncoder.encode(password), firstName, lastName);
        user.setConfirmationToken(UUID.randomUUID().toString());
        
        User saved = userRepository.save(user);

        String displayName = (firstName != null && !firstName.isBlank()) ? firstName : username;
        try {
            mailService.sendAccountConfirmationEmail(saved.getEmail(), displayName, saved.getConfirmationToken());
        } catch (Exception e) {
            userRepository.deleteById(saved.getId());
            throw new RuntimeException("Registration failed: unable to send confirmation email. Please verify mail configuration and try again.");
        }

        return saved;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByConfirmationToken(String token) {
        return userRepository.findByConfirmationToken(token);
    }

    public User confirmUser(String token) {
        Optional<User> userOpt = userRepository.findByConfirmationToken(token);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid confirmation token");
        }

        User user = userOpt.get();
        user.setIsConfirmed(true);
        user.setConfirmationToken(null);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
