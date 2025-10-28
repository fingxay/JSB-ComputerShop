package com.computershop.main.repositories;

import com.computershop.main.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    /**
     * Find role by role name
     */
    Optional<Role> findByRoleName(String roleName);
    
    /**
     * Check if role exists by role name
     */
    boolean existsByRoleName(String roleName);
    
    /**
     * Custom query to find role by name (case insensitive)
     */
    @Query("SELECT r FROM Role r WHERE LOWER(r.roleName) = LOWER(:roleName)")
    Optional<Role> findByRoleNameIgnoreCase(@Param("roleName") String roleName);
}