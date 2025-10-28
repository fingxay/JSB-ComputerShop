package com.computershop.main.services;

import com.computershop.main.entities.User;
import com.computershop.main.entities.Role;
import com.computershop.main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleService roleService;
    
    // Uncomment when Spring Security is configured
    // @Autowired
    // private PasswordEncoder passwordEncoder;
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }
    
    /**
     * Get user by username
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Get user by username or email (for login)
     */
    public Optional<User> getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }
    
    /**
     * Get users by role
     */
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Get users by role name
     */
    public List<User> getUsersByRoleName(String roleName) {
        return userRepository.findByRoleName(roleName);
    }
    
    /**
     * Create new user
     */
    public User createUser(User user) {
        // Validate username and email uniqueness
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username '" + user.getUsername() + "' already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email '" + user.getEmail() + "' already exists");
        }
        
        // Set default role if not specified
        if (user.getRole() == null) {
            user.setRole(roleService.getCustomerRole());
        }
        
        // Hash password (uncomment when Spring Security is configured)
        // user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        
        return userRepository.save(user);
    }
    
    /**
     * Register new customer
     */
    public User registerCustomer(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(password); // Will be hashed in createUser method
        user.setRole(roleService.getCustomerRole());
        
        return createUser(user);
    }
    
    /**
     * Update user
     */
    public User updateUser(Integer userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Check username uniqueness (excluding current user)
        if (!user.getUsername().equals(userDetails.getUsername()) && 
            userRepository.existsByUsername(userDetails.getUsername())) {
            throw new RuntimeException("Username '" + userDetails.getUsername() + "' already exists");
        }
        
        // Check email uniqueness (excluding current user)
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Email '" + userDetails.getEmail() + "' already exists");
        }
        
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Change password
     */
    public void changePassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Hash new password (uncomment when Spring Security is configured)
        // user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordHash(newPassword); // Temporary: store as plain text
        
        userRepository.save(user);
    }
    
    /**
     * Delete user
     */
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
    
    /**
     * Check if username exists
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Count users by role
     */
    public long countUsersByRole(String roleName) {
        return userRepository.countByRoleName(roleName);
    }
    
    /**
     * Authenticate user (basic implementation)
     */
    public Optional<User> authenticate(String usernameOrEmail, String password) {
        Optional<User> user = getUserByUsernameOrEmail(usernameOrEmail);
        if (user.isPresent()) {
            // Simple password check (use passwordEncoder.matches() when Spring Security is configured)
            if (user.get().getPasswordHash().equals(password)) {
                return user;
            }
        }
        return Optional.empty();
    }
}