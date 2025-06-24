package com.example.StudentFinanceTracker.Service;

import com.example.StudentFinanceTracker.Model.User;
import com.example.StudentFinanceTracker.Repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User saveUser(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public boolean checkPassword(String raw, String hashed) {
        return passwordEncoder.matches(raw, hashed);
    }
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void saveResetToken(String email, String token) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setSecretKey(token);
        userRepository.save(user);
    }
    public boolean changePassword(String token, String currentPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findBySecretKey(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (checkPassword(currentPassword, user.getPassword())) {
                user.setPassword(newPassword);
                user.setSecretKey(null);
                saveUser(user);
                return true;
            }
        }
        return false;
    }
}
