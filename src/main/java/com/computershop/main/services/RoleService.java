package com.computershop.main.services;

import com.computershop.main.entities.Role;
import com.computershop.main.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    /**
     * Get all roles
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    /**
     * Get role by ID
     */
    public Optional<Role> getRoleById(Integer roleId) {
        return roleRepository.findById(roleId);
    }
    
    /**
     * Get role by name
     */
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
    
    /**
     * Get role by name (case insensitive)
     */
    public Optional<Role> getRoleByNameIgnoreCase(String roleName) {
        return roleRepository.findByRoleNameIgnoreCase(roleName);
    }
    
    /**
     * Create new role
     */
    public Role createRole(Role role) {
        if (roleRepository.existsByRoleName(role.getRoleName())) {
            throw new RuntimeException("Role with name '" + role.getRoleName() + "' already exists");
        }
        return roleRepository.save(role);
    }
    
    /**
     * Update role
     */
    public Role updateRole(Integer roleId, Role roleDetails) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        
        // Check if new name conflicts with existing roles (excluding current role)
        if (!role.getRoleName().equals(roleDetails.getRoleName()) && 
            roleRepository.existsByRoleName(roleDetails.getRoleName())) {
            throw new RuntimeException("Role with name '" + roleDetails.getRoleName() + "' already exists");
        }
        
        role.setRoleName(roleDetails.getRoleName());
        return roleRepository.save(role);
    }
    
    /**
     * Delete role by ID
     */
    public void deleteRole(Integer roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new RuntimeException("Role not found with id: " + roleId);
        }
        roleRepository.deleteById(roleId);
    }
    
    /**
     * Check if role exists by name
     */
    public boolean existsByRoleName(String roleName) {
        return roleRepository.existsByRoleName(roleName);
    }
    
    /**
     * Get default customer role
     */
    public Role getCustomerRole() {
        return roleRepository.findByRoleName("customer")
                .orElseThrow(() -> new RuntimeException("Customer role not found"));
    }
    
    /**
     * Get admin role
     */
    public Role getAdminRole() {
        return roleRepository.findByRoleName("admin")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
    }
}