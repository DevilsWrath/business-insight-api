package de.savas.businessinsightapi.api.admin;

import de.savas.businessinsightapi.domain.security.Permission;
import de.savas.businessinsightapi.domain.security.Role;
import de.savas.businessinsightapi.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class AdminRbacDtos {

    public record PermissionResponse(
            Long id,
            String code,
            String description
    ) {
        public static PermissionResponse from(Permission permission) {
            return new PermissionResponse(permission.getId(), permission.getCode(), permission.getDescription());
        }
    }

    public record RoleResponse(
            Long id,
            String name,
            String description,
            List<String> permissionCodes
    ) {
        public static RoleResponse from(Role role) {
            List<String> permissionCodes = role.getPermissions().stream()
                    .map(Permission::getCode)
                    .sorted()
                    .toList();

            return new RoleResponse(role.getId(), role.getName(), role.getDescription(), permissionCodes);
        }
    }

    public record UserRoleResponse(
            Long id,
            String username,
            String email,
            List<String> roleNames
    ) {
        public static UserRoleResponse from(User user) {
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .sorted()
                    .toList();

            return new UserRoleResponse(user.getId(), user.getUsername(), user.getEmail(), roleNames);
        }
    }

    public record CreateRoleRequest(
            @NotBlank String name,
            String description
    ) {}

    public record UpdateRoleRequest(
            @NotBlank String description
    ) {}

    public record ReplaceRolePermissionsRequest(
            @NotEmpty List<String> permissionCodes
    ) {}

    public record ReplaceUserRolesRequest(
            @NotEmpty List<String> roleNames
    ) {}
}
