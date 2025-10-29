package com.computershop.main.services;

import com.computershop.main.entities.User;
import com.computershop.main.entities.Role;
import com.computershop.main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    
    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Register a new user
     */
    public User registerUser(User user) {
        // Hash password (simplified - in production use BCryptPasswordEncoder)
        user.setPasswordHash(hashPassword(user.getPassword()));
        user.setPassword(null); // Clear plain text password
        return userRepository.save(user);
    }
    
    /**
     * Validate user login
     */
    public Optional<User> validateLogin(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            // Try by email
            userOpt = userRepository.findByEmail(username);
        }
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (verifyPassword(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Hash password (simplified implementation)
     * In production, use BCryptPasswordEncoder
     */
    private String hashPassword(String password) {
        // Simple hash for demo - use BCrypt in production
        return password + "_hashed";
    }
    
    /**
     * Verify password (public method for controller use)
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        // Simple verification for demo - use BCrypt in production
        return (plainPassword + "_hashed").equals(hashedPassword);
    }
    
    /**
     * Update user (overloaded method)
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Get total number of users
     */
    public long getTotalUsers() {
        return userRepository.count();
    }
    
    /**
     * Get recent users
     */
    public List<User> getRecentUsers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return userRepository.findRecentUsers(pageable);
    }
    
    /**
     * Toggle user status (enable/disable)
     * Note: This is a simple implementation - you might want to add a status field to User entity
     */
    public void toggleUserStatus(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            // For now, just update the user (could add active/inactive status field)
            User user = userOpt.get();
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
    
}