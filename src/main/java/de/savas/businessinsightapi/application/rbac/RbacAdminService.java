package de.savas.businessinsightapi.application.rbac;

import de.savas.businessinsightapi.common.error.BadRequestException;
import de.savas.businessinsightapi.domain.security.Permission;
import de.savas.businessinsightapi.domain.security.PermissionRepository;
import de.savas.businessinsightapi.domain.security.Role;
import de.savas.businessinsightapi.domain.security.RoleRepository;
import de.savas.businessinsightapi.domain.user.User;
import de.savas.businessinsightapi.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RbacAdminService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RbacAdminService(PermissionRepository permissionRepository,
                            RoleRepository roleRepository,
                            UserRepository userRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Permission> listPermissions() {
        return permissionRepository.findAll().stream()
                .sorted(Comparator.comparing(Permission::getCode))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Role> listRoles() {
        return roleRepository.findAll().stream()
                .sorted(Comparator.comparing(Role::getName))
                .toList();
    }

    @Transactional
    public Role createRole(String name, String description) {
        if (roleRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Role already exists: " + name);
        }
        return roleRepository.save(new Role(name, description));
    }

    @Transactional
    public Role updateRoleDescription(Long roleId, String description) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BadRequestException("Role not found"));
        role.setDescription(description);
        return roleRepository.save(role);
    }

    @Transactional
    public Role replaceRolePermissions(Long roleId, List<String> permissionCodes) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BadRequestException("Role not found"));

        List<String> normalizedCodes = permissionCodes.stream().distinct().toList();
        List<Permission> permissions = permissionRepository.findAllByCodeIn(normalizedCodes);
        if (permissions.size() != normalizedCodes.size()) {
            throw new BadRequestException("One or more permission codes are invalid");
        }

        role.replacePermissions(new HashSet<>(permissions));
        return roleRepository.save(role);
    }

    @Transactional(readOnly = true)
    public List<User> listUsers(String query) {
        List<User> users;
        if (query == null || query.isBlank()) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
        }

        return users.stream()
                .sorted(Comparator.comparing(User::getUsername))
                .toList();
    }

    @Transactional
    public User replaceUserRoles(Long userId, List<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        List<String> normalizedNames = roleNames.stream().distinct().toList();
        List<Role> roles = roleRepository.findAllByNameIn(normalizedNames);
        if (roles.size() != normalizedNames.size()) {
            throw new BadRequestException("One or more role names are invalid");
        }

        Set<Role> roleSet = new HashSet<>(roles);
        user.replaceRoles(roleSet);
        return userRepository.save(user);
    }
}
